import React, { useMemo } from 'react';

const LanguageSelector = ({ selectedLanguage, onLanguageChange }) => {
    const languages = useMemo(() => [
        { label: "Python", value: "python" },
        { label: "JavaScript", value: "javascript" },
        { label: "Java", value: "java" },
        { label: "C++", value: "cpp" },
    ], []);

    return (
        <select
            value={selectedLanguage}
            onChange={onLanguageChange}
            className="language-selector"
        >
            {languages.map((lang) => (
                <option key={lang.value} value={lang.value}>
                    {lang.label}
                </option>
            ))}
        </select>
    );
};

export default LanguageSelector;
