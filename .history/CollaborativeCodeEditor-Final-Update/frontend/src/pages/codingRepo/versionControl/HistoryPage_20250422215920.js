import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';

function HistoryPage() {
const { repoId } = useParams();
const [versions, setVersions] = useState([]);
const [loading, setLoading] = useState(true);
const [error, setError] = useState(null);

useEffect(() => {
axios.get(`/api/viewer/repo/${repoId}/file-versions`)
.then(res => {
setVersions(res.data.fileVersions);
setLoading(false);
})
.catch(err => {
setError(err);
setLoading(false);
});
}, [repoId]);

if (loading) return Loading history...;
if (error) return Error loading history.;

return (

Repository History
{versions.map((v, idx) => (


Repo: {v.repoId} | Project: {v.projectName} | File: {v.filename} |
Modified At: {new Date(v.lastModifiedAt).toLocaleString()}



{v.content}



))}

);
}

export default HistoryPage;

// ==== BACKEND ==== // File: com/collaborative/editor/controller/FileVersionController.java package com.collaborative.editor.controller;

import com.collaborative.editor.model.File; import com.collaborative.editor.service.FileVersionService; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.\*;

import java.util.List; import java.util.Map;

@RestController @RequestMapping("/api/viewer") @CrossOrigin public class FileVersionController { private final FileVersionService fileVersionService;

```
@Autowired
public FileVersionController(FileVersionService fileVersionService) {
    this.fileVersionService = fileVersionService;
}

@GetMapping("/repo/{repoId}/file-versions")
public ResponseEntity<Map<String, List<File>>> getAllFileVersions(
        @PathVariable String repoId) {
    List<File> versions = fileVersionService.findByRepoIdOrderByLastModifiedAtDesc(repoId);
    return ResponseEntity.ok(Map.of("fileVersions", versions));
}
```

}

// File: com/collaborative/editor/service/FileVersionService.java package com.collaborative.editor.service;

import com.collaborative.editor.model.File; import java.util.List;

public interface FileVersionService { List findByRepoIdOrderByLastModifiedAtDesc(String repoId); }

// File: com/collaborative/editor/service/impl/FileVersionServiceImpl.java package com.collaborative.editor.service.impl;

import com.collaborative.editor.model.File; import com.collaborative.editor.repository.FileVersionRepository; import com.collaborative.editor.service.FileVersionService; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.stereotype.Service;

import java.util.List;

@Service public class FileVersionServiceImpl implements FileVersionService { private final FileVersionRepository repo;

```
@Autowired
public FileVersionServiceImpl(FileVersionRepository repo) {
    this.repo = repo;
}

@Override
public List<File> findByRepoIdOrderByLastModifiedAtDesc(String repoId) {
    return repo.findByRepoIdOrderByLastModifiedAtDesc(repoId);
}
```

}

// File: com/collaborative/editor/repository/FileVersionRepository.java package com.collaborative.editor.repository;

import com.collaborative.editor.model.File; import org.springframework.data.mongodb.repository.MongoRepository; import java.util.List;

public interface FileVersionRepository extends MongoRepository\<File, String> { List findByRepoIdOrderByLastModifiedAtDesc(String repoId); }

// ==== FRONTEND ==== // src/App.jsx (add route) import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'; import RepoPage from './pages/RepoPage'; import HistoryPage from './pages/HistoryPage';

function App() { return (   \<Route path="/repo/\:repoId" element={} /> \<Route path="/repo/\:repoId/history" element={} />   ); }

export default App;

// src/pages/RepoPage.jsx (add button) import { useNavigate, useParams } from 'react-router-dom';

function RepoPage() { const { repoId } = useParams(); const navigate = useNavigate();

return (  {/\* existing repo UI \*/} \<button onClick={() => navigate(`/repo/${repoId}/history`)} className="px-4 py-2 bg-blue-600 text-white rounded" > View Code History   ); }

export default RepoPage;

// src/pages/HistoryPage.jsx import React, { useEffect, useState } from 'react'; import { useParams } from 'react-router-dom'; import axios from 'axios';

function HistoryPage() { const { repoId } = useParams(); const [versions, setVersions] = useState([]); const [loading, setLoading] = useState(true); const [error, setError] = useState(null);

useEffect(() => { axios.get(`/api/viewer/repo/${repoId}/file-versions`) .then(res => { setVersions(res.data.fileVersions); setLoading(false); }) .catch(err => { setError(err); setLoading(false); }); }, [repoId]);

if (loading) return Loading history...; if (error) return Error loading history.;

return (  Repository History {versions.map((v, idx) => (   Repo: {v.repoId} | Project: {v.projectName} | File: {v.filename} | Modified At: {new Date(v.lastModifiedAt).toLocaleString()}    {v.content}    ))}  ); }

export default HistoryPage;

