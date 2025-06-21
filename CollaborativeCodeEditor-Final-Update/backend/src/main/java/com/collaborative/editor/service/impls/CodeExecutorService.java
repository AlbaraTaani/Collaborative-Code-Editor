package com.collaborative.editor.service.impls;

import com.collaborative.editor.dto.CodeExecution;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.Map;

@Service
public class CodeExecutorService {

    private static final String CODE_PLACEHOLDER = "{code}";
    private static final String INPUT_PLACEHOLDER = "{input}";
    private static final String EMPTY_INPUT_PLACEHOLDER = "";

    private Map<String, Map<String, String>> dockerCommands;

    @PostConstruct
    public void loadDockerCommands() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream templateStream = new ClassPathResource("data/dockerCommands.json").getInputStream()) {
            dockerCommands = objectMapper.readValue(templateStream, new TypeReference<>() {});
        }
    }

    public String executeCode(CodeExecution codeRequest) throws Exception {
        Map<String, String> dockerCommands = getDockerCommandForLanguage(codeRequest.getLanguage());

        String command = prepareCommand(dockerCommands.get("dockerCommand"), codeRequest.getCode(), codeRequest.getInput());
        String[] dockerCommand = buildDockerCommand(dockerCommands.get("dockerImage"), command);

        return runDockerCommand(dockerCommand);
    }

    private Map<String, String> getDockerCommandForLanguage(String language) {
        Map<String, String> template = dockerCommands.get(language);
        if (template == null) {
            throw new IllegalArgumentException("Unsupported language: " + language);
        }
        return template;
    }

    private String prepareCommand(String dockerCommand, String code, String input) {
        String escapedCode = escapeCode(code);
        String preparedCommand = dockerCommand.replace(CODE_PLACEHOLDER, escapedCode);

        if (input == null || input.isEmpty()) {
            return removeInputPlaceholder(preparedCommand);
        }
        return preparedCommand.replace(INPUT_PLACEHOLDER, input);
    }

    private String removeInputPlaceholder(String command) {
        return command.replace("echo \"" + INPUT_PLACEHOLDER + "\" | ", EMPTY_INPUT_PLACEHOLDER);
    }

    private String[] buildDockerCommand(String dockerImage, String command) {
        return new String[] { "docker", "run", "--rm", dockerImage, "bash", "-c", command };
    }

    private String runDockerCommand(String[] dockerCommand) throws Exception {
        Process process = new ProcessBuilder(dockerCommand).redirectErrorStream(true).start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder output = new StringBuilder();
            reader.lines().forEach(line -> output.append(line).append("\n"));

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                output.append("Error: ").append(exitCode);
            }

            return output.toString();
        }
    }

    private String escapeCode(String code) {
        return code.replace("\"", "\\\"").replace("$", "\\$");
    }
}
