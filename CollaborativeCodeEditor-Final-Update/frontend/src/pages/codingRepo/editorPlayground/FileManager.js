import { useState, useCallback } from 'react';

const useFileManager = (code, currentFile, repo, setCode, setShowConfirmMergeModal, setShowConfirmPushModal) => {
    const [successMessage, setSuccessMessage] = useState('');

    const pushFileToServer = useCallback(async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/files/push-file-content`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${localStorage.getItem('token')}` },
                body: JSON.stringify(
                    { filename:currentFile.filename,
                        repoId:repo.repoId,
                        projectName:currentFile.projectName,
                        content: code,
                        createdAt: currentFile.createdAt,
                        lastModifiedAt: currentFile.lastModifiedAt,
                        extension: currentFile.extension
                    }),
            });
            if (!response.ok) throw new Error('Failed to push to server');
            setSuccessMessage('Pushed to server successfully!');
            setTimeout(() => setSuccessMessage(''), 3000);
            setShowConfirmPushModal(false);
        } catch (error) {
            console.error('Push error:', error);
            setSuccessMessage('Push error!');
        }
    }, [code, currentFile.filename, currentFile.projectName, repo.repoId]);

    const mergeFileFromServer = useCallback(async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/files/merge-file-content`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${localStorage.getItem('token')}` },
                body: JSON.stringify({ filename:currentFile.filename, repoId:repo.repoId, projectName:currentFile.projectName, content: code }),
            });
            if (!response.ok) throw new Error('Failed to merge from server');
            const data = await response.json();
            setCode(data.file.content);
            setTimeout(() => setSuccessMessage(''), 3000);
            setShowConfirmMergeModal(false);
        } catch (error) {
            console.error('Merge error:', error);
        }
    }, [code, currentFile.filename, currentFile.projectName, repo.repoId, setCode]);

    return { pushFileToServer, mergeFileFromServer, successMessage };
};

export default useFileManager;
