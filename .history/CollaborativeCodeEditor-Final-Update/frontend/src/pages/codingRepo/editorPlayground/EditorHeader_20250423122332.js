import React, { useState } from 'react';
import LanguageSelector from './LanguageSelector';

const EditorHeader = ({ selectedLanguage, onLanguageChange, repo }) => {


    return (
        <div className="editor-header">
            <div className="repo-info">
                <h2>{repo.name}</h2>
                <div className="repo-actions">
                    <div className="actions">
                        {/* <label htmlFor="language-select" className="language-label">Language</label> */}
                        <LanguageSelector
                            selectedLanguage={selectedLanguage}
                            onLanguageChange={onLanguageChange}
                        />
                    </div>
                </div>
            </div>

            {/* {showToast && (
                <div className="toast">
                    <p>Repo ID copied to clipboard!</p>
                </div>
            )} */}
        </div>
    );
};

export default EditorHeader;


