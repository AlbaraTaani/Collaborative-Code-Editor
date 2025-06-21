// import React, { useState, useEffect } from 'react';
// import { useNavigate } from 'react-router-dom';
// import './Home.css';

// const Home = () => {
//     const [darkMode, setDarkMode] = useState(false);
//     const [user, setUser] = useState({ name: '', profileImage: '' });
//     const navigate = useNavigate();

//     // Fetch user info from the backend
//     const fetchUserInfo = async () => {
//         try {
//             const response = await fetch('http://localhost:8080/api/user/info', {
//                 method: 'GET',
//                 headers: {
//                     Authorization: `Bearer ${localStorage.getItem('token')}`,
//                 },
//             });
//             if (!response.ok) throw new Error('Failed to fetch user info');

//             const userInfo = await response.json();

//             setUser({ name: userInfo.username, profileImage: userInfo.profileImage });

//         } catch (error) {
//             console.error('Failed to fetch user info:', error);
//         }
//     };

//     useEffect(() => {
//         fetchUserInfo();
//     }, []);

//     const toggleTheme = () => setDarkMode(!darkMode);

//     const handleLogout = () => {
//         localStorage.removeItem('token');
//         navigate('/');
//     };

//     const navigateToCreateRepo = () => navigate('/create-repo', { state: { user } });
//     const navigateToJoinRepo = () => navigate('/join-repo', { state: { user } });
//     const navigateToEditRepo = () => navigate('/edit-repo', { state: { user } });

//     return (
//         <div className={`home-container ${darkMode ? 'light-mode' : 'dark-mode'}`}>
//             <header className="home-header">
//                 <div className="user-info">
//                     <img className="user-image" src={user.profileImage} alt="User" />
//                     <span className="user-name">{user.name}</span>
//                 </div>
//                 <div className="header-buttons">
//                     <button className="theme-toggle-btn" onClick={toggleTheme}>
//                         {darkMode ? 'Switch to Dark Mode' : 'Switch to Light Mode'}
//                     </button>
//                     <button className="logout-btn" onClick={handleLogout}>
//                         LOG OUT
//                     </button>
//                 </div>
//             </header>

//             <div className="home-body">
//                 {/* Image displayed above the buttons and title */}
//                 <div className="home-image"></div>

//                 <div className="buttons-title-container">
//                     <div className="buttons-container">
//                         <button className="btn" onClick={navigateToCreateRepo}>
//                             Create Repo
//                         </button>
//                         <button className="btn" onClick={navigateToJoinRepo}>
//                             Join Repo
//                         </button>
//                         <button className="btn" onClick={navigateToEditRepo}>
//                             Edit Repo
//                         </button>
//                     </div>
//                     <div className="title-container">
//                         <h1 className="title">Collaborative Code Editor</h1>
//                         <p className="subtitle">Real-time collaboration and version control</p>
//                     </div>
//                 </div>
//             </div>
//         </div>
//     );
// };

// export default Home;

import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { FaSun, FaMoon, FaSignOutAlt } from "react-icons/fa";
import "./Home.css";

const Home = () => {
  const [darkMode, setDarkMode] = useState(false);
  const [user, setUser] = useState({ name: "", profileImage: "" });
  const navigate = useNavigate();

  // Fetch user info from the backend
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
  // ... (keep all imports and component logic the same)

  return (
    <div className={`home-container ${darkMode ? "light-mode" : "dark-mode"}`}>
      <header className="home-header">
        <div className="logo-container">
          <img src="/logo.svg" alt="App Logo" className="app-logo" />
        </div>

        {/* ... (keep header buttons the same) */}
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
  // return (
  //   <div className={`home-container ${darkMode ? 'light-mode' : 'dark-mode'}`}>
  //     <header className="home-header">
  //       <div className="logo-container">
  //         <img src="/logo.svg" alt="App Logo" className="app-logo" />
  //       </div>

  //       <div className="header-buttons">
  //         {/* Theme toggle */}
  //         <button
  //           className="icon-btn theme-btn"
  //           title={darkMode ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
  //           onClick={toggleTheme}
  //         >
  //           {darkMode ? <FaSun size={20} /> : <FaMoon size={20} />}
  //         </button>

  //         {/* Log Out */}
  //         <button
  //           className="icon-btn"
  //           title="Log Out"
  //           onClick={handleLogout}
  //         >
  //           <FaSignOutAlt size={20} />
  //         </button>
  //       </div>
  //     </header>

  //     <div className="home-body">
  //       <div className="home-image" />

  //       <div className="buttons-title-container">
  //         <div className="buttons-container">
  //           <button className="btn" onClick={() => navigateTo('/create-repo')}>
  //             Create Repo
  //           </button>
  //           <button className="btn" onClick={() => navigateTo('/join-repo')}>
  //             Join Repo
  //           </button>
  //           <button className="btn" onClick={() => navigateTo('/edit-repo')}>
  //             Edit Repo
  //           </button>
  //         </div>
  //         <div className="title-container">
  //           <h1 className="title">Collaborative Code Editor</h1>
  //           <p className="subtitle">Real-time collaboration and version control</p>
  //         </div>
  //       </div>
  //     </div>
  //   </div>
  // );
};

export default Home;
