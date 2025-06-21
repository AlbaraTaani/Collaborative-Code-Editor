import React, { useState, useEffect } from 'react';
import ProjectList from './ProjectList';
import Action from "./Action";

import { useNavigate, useParams } from 'react-router-dom';


const Control = ({ repo, currentFile, isConnected, subscribeToCodeUpdates }) => {

    
    const [projects, setProjects] = useState([]);
    const [selectedProject, setSelectedProject] = useState(null);
    const [files, setFiles] = useState([]);
    const [selectedFile, setSelectedFile] = useState(null);
    const [selectedFileContent, setSelectedFileContent] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [showConfirmModal, setShowConfirmModal] = useState(false);
    const [fileToFetch, setFileToFetch] = useState(null);

    const [showProjectModal, setShowProjectModal] = useState(false);
    const [showFileModal, setShowFileModal] = useState(false);
    const [showDownloadModal, setShowDownloadModal] = useState(false);
    const showError = (message) => {
        setErrorMessage(message);
        setTimeout(() => setErrorMessage(''), 3000);
    };

    const downloadProject = async ({ projectName }) => {
        try {
            const response = await fetch(`http://localhost:8080/api/projects/${repo.repoId}/${projectName}/download-project`, {
                method: 'GET',
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
            });

            if (!response.ok) throw new Error('Failed to download project');

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `${projectName}.zip`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
        } catch (error) {
            showError('You do not have projects currently. Please add a new project.');
        }
    };



    const fetchProjects = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/projects/repo/${repo.repoId}`, {
                method: 'GET',
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
            });
            if (!response.ok) throw new Error('Failed to fetch projects');
            const data = await response.json();
            setProjects(data.projects);
        } catch (error) {
            showError('You do not have projects currently. Please add a new project.');
        }
    };


    const fetchFiles = async (projectName) => {
        try {
            const encodedProjectName = encodeURIComponent(projectName);
            const response = await fetch(`http://localhost:8080/api/files/list-files/${encodedProjectName}/${repo.repoId}`, {
                method: 'GET',
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
            });
            if (!response.ok) throw new Error('Failed to fetch files');
            const data = await response.json();
            setFiles(data.files);
        } catch (error) {
            showError('You do not have files currently. Please add a new file.');
        }
    };

    const fetchFileContent = async (file) => {
        try {
            const response = await fetch(`http://localhost:8080/api/files/pull-file-content`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
                body: JSON.stringify({
                    filename: file.filename,
                    repoId: file.repoId,
                    projectName: file.projectName
                }),
            });
            if (!response.ok) throw new Error('Failed to fetch file content');

            const data = await response.json();
            setSelectedFileContent(data.file.content);
            currentFile(data.file);
            if (isConnected && data.file) {
                subscribeToCodeUpdates(data.file);
            }
        } catch (error) {
            showError('The file does not have content currently...');
        }
    };

    const handleProjectSelect = (project) => {
        setSelectedProject(selectedProject === project ? null : project);
        setFiles([]);
        setSelectedFile(null);
        if (selectedProject !== project) {
            fetchFiles(project.projectName);
        }
    };

    const handleFileSelect = (file) => {
        setFileToFetch(file);
        setShowConfirmModal(true);
    };

    const handleConfirmPull = () => {
        setSelectedFile(fileToFetch);
        fetchFileContent(fileToFetch);
        setShowConfirmModal(false);
    };

    const handleCancelPull = () => setShowConfirmModal(false);

    const handleCreateNewProject = async (projectName) => {
        try {
            const response = await fetch(`http://localhost:8080/api/projects/create-project`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                },
                body: JSON.stringify({ projectName: projectName, repoId: repo.repoId }),
            });
            if (!response.ok) throw new Error('Failed to create project');
            fetchProjects();
        } catch (error) {
            showError('Failed to create new project.');
        }
    };

    const handleCreateNewFile = async ({ projectName, filename, extension }) => {
        if (filename && projectName) {
            try {
                const response = await fetch(`http://localhost:8080/api/files/create-file`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${localStorage.getItem('token')}`,
                    },
                    body: JSON.stringify({ filename: filename, repoId: repo.repoId, projectName: projectName, extension: extension }),
                });
                if (!response.ok) throw new Error('Failed to create file');
                fetchFiles(projectName);
            } catch (error) {
                showError('Failed to create new file.');
            }
        }
    };

    useEffect(() => {
        if (repo.repoId) {
            fetchProjects();
        }
    }, [repo.repoId]);

    return (
        <div className="version-control">
            <h2>Version Control</h2>
            <div className="version-control-buttons">
                <button className="action-btn add-project-btn" onClick={() => setShowProjectModal(true)}>
                    Add New Project
                </button>
                <button className="action-btn add-file-btn" onClick={() => setShowFileModal(true)}>
                    Add New File
                </button>
                <button className="action-btn add-file-btn" onClick={() => setShowDownloadModal(true)}>
                    DownloadProject
                </button>
            </div>

            <ProjectList
                projects={projects}
                selectedProject={selectedProject}
                onSelectProject={handleProjectSelect}
                files={files}
                selectedFile={selectedFile}
                onSelectFile={handleFileSelect}
            />

            <Action
                show={showProjectModal}
                title="Create New Project"
                actionLabel="Create"
                inputs={[{ label: 'Project Name', placeholder: 'Enter new project name', name: 'projectName' }]}
                onConfirm={(inputValues) => {
                    handleCreateNewProject(inputValues.projectName);
                    setShowProjectModal(false);
                }}
                onCancel={() => setShowProjectModal(false)}
            />

            <Action
                show={showFileModal}
                title="Create New File"
                actionLabel="Create"
                inputs={[
                    { label: 'Project Name', placeholder: '/ Enter project name', name: 'projectName' },
                    { label: 'File Name', placeholder: 'Enter new file name ', name: 'filename' },
                    { label: 'File Extension', placeholder: 'Enter Extension ', name: 'extension' },
                ]}
                onConfirm={(inputValues) => {
                    handleCreateNewFile(inputValues);
                    setShowFileModal(false);
                }}
                onCancel={() => setShowFileModal(false)}
            />

            <Action
                show={showDownloadModal}
                title="Download Project"
                actionLabel="Download"
                inputs={[
                    { label: 'Project Name', placeholder: '/ Enter project name', name: 'projectName' },

                ]}
                onConfirm={(inputValues) => {
                    downloadProject(inputValues);
                    setShowDownloadModal(false);
                }}
                onCancel={() => setShowDownloadModal(false)}
            />

            <Action
                show={showConfirmModal}
                title="Confirm PULL"
                actionLabel="PULL"
                inputs={[]}
                onConfirm={handleConfirmPull}
                onCancel={handleCancelPull}
            />
        </div>
    );
};

export default Control;
