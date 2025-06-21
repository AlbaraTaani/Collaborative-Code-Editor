package com.collaborative.editor.controller;
com/collaborative/editor/controller/FileVersionController.java
package com.collaborative.editor.controller;

import com.collaborative.editor.model.File;
import com.collaborative.editor.service.FileVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


//new
@RestController
@RequestMapping("/api/viewer")
@CrossOrigin
public class FileVersionController {
private final FileVersionService fileVersionService;