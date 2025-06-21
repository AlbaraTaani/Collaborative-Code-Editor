// import React, { useEffect, useState } from 'react';
// import { useParams } from 'react-router-dom';
// import axios from 'axios';

// function HistoryPage() {
//   const { repoId } = useParams();
//   const [versions, setVersions] = useState([]);
//   const [loading, setLoading] = useState(true);
//   const [error, setError] = useState(null);

//   useEffect(() => {
//     axios.get(`http://localhost:8080/api/viewer/repo/${repoId}/file-versions`)
//       .then(res => {
//         setVersions(res.data.fileVersions);
//       })
//       .catch(err => {
//         setError(err);
//       })
//       .finally(() => {
//         setLoading(false);
//       });
//   }, [repoId]);

//   if (loading) return <p>Loading history...</p>;
//   if (error) return <p>Error loading history.</p>;

//   return (
//     <div className="p-4">
//       <h1 className="text-2xl font-bold mb-4">Repository History</h1>
//       {versions.map((v, idx) => (
//         <div key={idx} className="mb-6 p-4 border rounded shadow-sm">
//           <p className="text-sm text-gray-500">
//             Repo: {v.repoId} | Project: {v.projectName} | File: {v.filename} | Modified At: {new Date(v.lastModifiedAt).toLocaleString()}
//           </p>
//           <pre className="mt-2 bg-gray-100 p-2 rounded overflow-x-auto">
//             <code>{v.content}</code>
//           </pre>
//         </div>
//       ))}
//     </div>
//   );
// }

// export default HistoryPage;
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';

function HistoryPage() {
  const { repoId } = useParams();
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    axios.get(`http://localhost:8080/api/viewer/repo/${repoId}/history`)
      .then(res => setLogs(res.data.repoLogs))
      .catch(err => setError(err))
      .finally(() => setLoading(false));
  }, [repoId]);

  if (loading) return <p>Loading history...</p>;
  if (error) return <p>Error loading history.</p>;

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">Full Repository History</h1>
      {logs.map((log, idx) => (
        <div key={idx} className="mb-6 p-4 border rounded shadow-sm">
          <p className="text-sm text-gray-500">
            File: {log.filename} | Project: {log.projectName} |
            At: {new Date(log.timestamp).toLocaleString()}
          </p>
          <pre className="mt-2 bg-gray-100 p-2 rounded overflow-x-auto">
            <code>{log.content}</code>
          </pre>
        </div>
      ))}
    </div>
  );
}

export default HistoryPage;