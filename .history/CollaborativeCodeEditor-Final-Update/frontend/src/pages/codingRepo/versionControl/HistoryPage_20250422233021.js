import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';

function HistoryPage() {
  const { repoId } = useParams();
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    axios.get(`http://localhost:8080/api/viewer/repo/${repoId}/code-history`)
      .then(res => setHistory(res))
      .catch(err => setError(err))
      .finally(() => setLoading(false));
  }, [repoId]);

  if (loading) return <p>Loading code history...</p>;
  if (error) return <p>Error loading code history.</p>;
  console.log(history);
  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">Full Code History</h1>
      {history.map((file, idx) => (
        <div key={idx} className="mb-6 p-4 border rounded shadow-sm">
          <p className="text-sm text-gray-500">
            Repo: {file.repoId} | Project: {file.projectName} | File: {file.filename} | Modified At: {new Date(file.lastModifiedAt).toLocaleString()}
          </p>
          <pre className="mt-2 bg-gray-100 p-2 rounded overflow-x-auto">
            <code>{file.content}</code>
          </pre>
        </div>
      ))}
    </div>
  );
}

export default HistoryPage;