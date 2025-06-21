import React, { useEffect, useState } from 'react'; 
import { useParams } from 'react-router-dom'; 
import axios from 'axios';

function HistoryPage() { 
    const { repoId } = useParams();
     const [versions, setVersions] = useState([]);
      const [loading, setLoading] = useState(true);
       const [error, setError] = useState(null);

    useEffect(() => { axios.get(`/api/viewer/repo/${repoId}/file-versions`)
.   es => { setVersions(res.data.fileVersions); setLoading(false); }) .catch(err => { setError(err); setLoading(false); }); }, [repoId]);

if (loading) return Loading history...; if (error) return Error loading history.;

return (  Repository History {versions.map((v, idx) => (   Repo: {v.repoId} | Project: {v.projectName} | File: {v.filename} | Modified At: {new Date(v.lastModifiedAt).toLocaleString()}    {v.content}    ))}  ); }

export default HistoryPage;

