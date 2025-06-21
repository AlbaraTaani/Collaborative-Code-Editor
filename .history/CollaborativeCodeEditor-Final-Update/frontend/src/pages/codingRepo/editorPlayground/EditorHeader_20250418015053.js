import React, { useState } from 'react';
import LanguageSelector from './LanguageSelector';

const EditorHeader = ({ selectedLanguage, onLanguageChange, repo }) => {
    // const [showToast, setShowToast] = useState(false);

    // const handleCopyRepoId = () => {
    //     navigator.clipboard.writeText(repo.repoId);
    //     setShowToast(true);
    //     setTimeout(() => setShowToast(false), 3000);
    // };

    return (
        <div className="editor-header">
            <div className="repo-info">
                <h2>{repo.name}</h2>
                <div className="repo-actions">
                    {/* <div className="repo-id-container">
                        <span>{repo.repoId}</span>
                        <button className="copy-btn" onClick={handleCopyRepoId}>Copy ID</button>
                    </div> */}
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


