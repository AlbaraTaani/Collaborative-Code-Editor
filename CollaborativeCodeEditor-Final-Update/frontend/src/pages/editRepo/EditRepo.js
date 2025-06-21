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
               
                <h3 className="repo-name">{repo.name}</h3>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default EditRepo;
