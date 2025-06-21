import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { FaSun, FaMoon, FaHome, FaSignOutAlt } from "react-icons/fa";
import logo from "../../logo.svg";
import "./JoinRepo.css";

const JoinRepo = () => {
  const [darkMode, setDarkMode] = useState(false);
  const [viewerRepos, setViewerRepos] = useState([]);
  const [collaboratorRepos, setCollaboratorRepos] = useState([]);
  const navigate = useNavigate();
  const { user } = useLocation().state;

  useEffect(() => {
    (async () => {
      try {
        const res = await fetch(
          `http://localhost:8080/api/repos/join-repo?username=${user.name}`,
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("token")}`,
            },
          }
        );
        if (!res.ok) throw new Error();
        const data = await res.json();
        setViewerRepos(data.viewRepos);
        setCollaboratorRepos(data.collaborativeRepos);
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

  const handleRepoClick = async (repo, roleClick) => {
    try {
      const res = await fetch(
        `http://localhost:8080/api/repos/join-repo/${repo.repoId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
          body: JSON.stringify({ repoName: repo.name }),
        }
      );
      if (res.ok) {
        navigate(`/join-repo/${repo.repoId}/${roleClick}`, {
          state: {
            user,
            repo: { repoId: repo.repoId, name: repo.name },
            role: roleClick,
          },
        });
      }
    } catch (e) {
      console.error(e);
    }
  };

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

      <div className="repo-lists-container">
        <div className="repo-list">
          <h2>Join as VIEWER</h2>
          {viewerRepos.length === 0 ? (
            <p>You Can't join any repo as viewer</p>
          ) : (
            viewerRepos.map((repo) => (
              <div
                key={repo.repoId}
                className="repo-card"
                onClick={() => handleRepoClick(repo, "VIEWER")}
              >
                <h3>{repo.name}</h3>
                {/* <p>Repo Name: {repo.name}</p>
                <p>Repo Id: {repo.repoId}</p> */}
              </div>
            ))
          )}
        </div>

        <div className="repo-list">
          <h2>Join as E</h2>
          {collaboratorRepos.length === 0 ? (
            <p>You Can't join any repo as Collaborator</p>
          ) : (
            collaboratorRepos.map((repo) => (
              <div
                key={repo.repoId}
                className="repo-card"
                onClick={() => handleRepoClick(repo, "COLLABORATOR")}
              >
                {/* <p>Repo Name: {repo.name}</p> */}
                <h3>{repo.name}</h3>
                {/* <p>Repo Id: {repo.repoId}</p> */}
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default JoinRepo;
