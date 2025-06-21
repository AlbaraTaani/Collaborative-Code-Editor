// import React, { useState, useEffect } from 'react';
// import { useLocation, useNavigate } from 'react-router-dom';
// import './EditRepo.css';

// const EditRepo = () => {
//     const [repos, setRepos] = useState([]);
//     const [darkMode, setDarkMode] = useState(false);
//     const navigate = useNavigate();
//     const location = useLocation();
//     const { user } = location.state;

//     const fetchRepos = async () => {
//         try {
//             const response = await fetch(`http://localhost:8080/api/repos/edit-repo/${user.name}`, {
//                 method: 'GET',
//                 headers: {
//                     Authorization: `Bearer ${localStorage.getItem('token')}`,
//                 },
//             });
//             if (!response.ok) throw new Error('Failed to fetch repos');
//             const data = await response.json();
//             setRepos(data.repos);
//         } catch (error) {
//             console.error('Error fetching repos:', error);
//         }
//     };

//     useEffect(() => {
//         fetchRepos();
//     }, []);

//     const handleRepoClick = (repo) => {
//         navigate(`/edit-repo/repo/${repo.repoId}`, { state: { user, repo } });
//     };

//     const toggleTheme = () => setDarkMode(!darkMode);

//     const handleLogout = () => {
//         localStorage.removeItem('token');
//         navigate('/');
//     };
//     const handleHome = () => {
//         navigate('/Home');
//     };
//     return (
//         <div className={`repos-container ${darkMode ? 'light-mode' : 'dark-mode'}`}>
//             <header className="repos-header">
//                 <div className="user-info">
//                     <img className="user-image" src={user.profileImage} alt="User" />
//                     <span className="user-name">{user.name}</span>
//                 </div>
//                 <div className="header-buttons">
//                     <button className="theme-toggle-btn" onClick={toggleTheme}>
//                         {darkMode ? 'Switch to Dark Mode' : 'Switch to Light Mode'}
//                     </button>
//                     <button className="theme-toggle-btn" onClick={handleHome}>HOME</button>
//                     <button className="logout-btn" onClick={handleLogout}>LOG OUT</button>
//                 </div>
//             </header>

//             <div className="repos-body">
//                 {repos.length === 0 ? (
//                     <p>No repos found.</p>
//                 ) : (
//                     <div className="repos-grid">
//                         {repos.map((repo) => (
//                             <div key={repo.repoId} className="repo-card" onClick={() => handleRepoClick(repo)}>
//                                 <img
//                                     src="https://w7.pngwing.com/pngs/687/710/png-transparent-conference-centre-meeting-convention-table-computer-icons-program-development-blue-text-symmetry-thumbnail.png"
//                                     alt={repo.name}
//                                     className="repo-image"
//                                 />
//                                 <h3 className="repo-name">{repo.name}</h3>
//                                 <p className="repo-id">ID: {repo.repoId}</p>
//                             </div>
//                         ))}
//                     </div>
//                 )}
//             </div>
//         </div>
//     );
// };

// export default EditRepo;

import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { FaSun, FaMoon, FaHome, FaSignOutAlt } from "react-icons/fa";
import logo from "../../logo.svg";
import "./EditRepo.css";

const EditRepo = () => {
  const [repos, setRepos] = useState([]);
  const [darkMode, setDarkMode] = useState(false);
  const navigate = useNavigate();
  const { user } = useLocation().state;

  useEffect(() => {
    (async () => {
      try {
        const res = await fetch(
          `http://localhost:8080/api/repos/edit-repo/${user.name}`,
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );
        if (!res.ok) throw new Error();
        const { repos } = await res.json();
        setRepos(repos);
      } catch (e) {
        console.error(e);
      }
    })();
  }, [user.name]);

  const toggleTheme = () => setDarkMode((m) => !m);
  const handleHome = () => navigate("/Home");
  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/");
  };
  const handleRepoClick = (repo) =>
    navigate(`/edit-repo/repo/${repo.repoId}`, {
      state: { user, repo },
    });

  return (
    <div className={`repos-container ${darkMode ? "light-mode" : "dark-mode"}`}>
      <header className="repos-header">
        <div className="logo-container">
          <img src={logo} alt="App Logo" className="app-logo" />
        </div>
        <div className="header-buttons">
          <button
            className="icon-btn theme-btn"
            title={darkMode ? "Switch to Light Mode" : "Switch to Dark Mode"}
            onClick={toggleTheme}
          >
            {darkMode ? <FaSun size={20} /> : <FaMoon size={20} />}
          </button>
          <button className="icon-btn" title="Home" onClick={handleHome}>
            <FaHome size={20} />
          </button>
          <button className="icon-btn" title="Log Out" onClick={handleLogout}>
            <FaSignOutAlt size={20} />
          </button>
        </div>
      </header>

      <div className="repos-body">
        {repos.length === 0 ? (
          <p>No repos found.</p>
        ) : (
          <div className="repos-grid">
            {repos.map((repo) => (
              <div
                key={repo.repoId}
                className="repo-card"
                onClick={() => handleRepoClick(repo)}
              >
                {/* <img
                  src="https://w7.pngwing.com/pngs/687/710/png-transparent-conference-centre-meeting-convention-table-computer-icons-program-development-blue-text-symmetry-thumbnail.png"
                  alt={repo.name}
                  className="repo-image"
                /> */}
                <h3 className="repo-name">{repo.name}</h3>
                {/* <p className="repo-id">ID: {repo.repoId}</p> */}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default EditRepo;
