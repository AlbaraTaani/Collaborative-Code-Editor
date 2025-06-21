import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { FaSun, FaMoon, FaSignOutAlt } from "react-icons/fa";
import "./Home.css";

const Home = () => {
  const [darkMode, setDarkMode] = useState(false);
  const [user, setUser] = useState({ name: "", profileImage: "" });
  const navigate = useNavigate();

  useEffect(() => {
    (async () => {
      try {
        const resp = await fetch("http://localhost:8080/api/user/info", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        if (!resp.ok) throw new Error();
        const info = await resp.json();
        setUser({ name: info.username, profileImage: info.profileImage });
      } catch {
        console.error("Failed to fetch user info");
      }
    })();
  }, []);

  const toggleTheme = () => setDarkMode((m) => !m);
  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/");
  };

  const navigateTo = (path) => navigate(path, { state: { user } });

  return (
    <div className={`home-container ${darkMode ? "light-mode" : "dark-mode"}`}>
      <header className="home-header">
        <div className="logo-container">
          <img src="/logo.svg" alt="App Logo" className="app-logo" />
        </div>

        <div className="header-buttons">
          {/* Theme toggle */}
          <button
            className="icon-btn theme-btn"
            title={darkMode ? "Switch to Light Mode" : "Switch to Dark Mode"}
            onClick={toggleTheme}
          >
            {darkMode ? <FaSun size={20} /> : <FaMoon size={20} />}
          </button>

          {/* Log Out */}
          <button className="icon-btn" title="Log Out" onClick={handleLogout}>
            <FaSignOutAlt size={20} />
          </button>
        </div>
      </header>

      <div className="home-body">
        <div className="buttons-container">
          <button className="btn" onClick={() => navigateTo("/create-repo")}>
            Create Repository
          </button>
          <button className="btn" onClick={() => navigateTo("/join-repo")}>
            Join Repositories
          </button>
          <button className="btn" onClick={() => navigateTo("/edit-repo")}>
            Manage Repositories
          </button>
        </div>
      </div>
    </div>
  );
};

export default Home;
