import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  FaHome,
  FaSignOutAlt,
  FaDoorOpen,
  FaMoon,
  FaSun,
} from 'react-icons/fa';
import './Header.css';

const Header = ({
  user,
  darkMode,
  toggleTheme,
  closeWebSocketConnection,
  sendChatMessage,
  repo,
  role,
}) => {
  const navigate = useNavigate();

  const leaveMessage = {
    sender: user.name,
    role,
    filename: '',
    repoId: repo.repoId,
    projectName: '',
    content: 'left the repo',
    type: 'leave',
  };

  const doLeave = (nextRoute) => {
    sendChatMessage(leaveMessage);
    closeWebSocketConnection();
    navigate(nextRoute, { state: nextRoute === '/join-repo' ? { user } : undefined });
  };

  const handleLogout = () => {
    doLeave('/');
    localStorage.removeItem('token');
  };

  const handleHome = () => doLeave('/Home');
  const handleLeaveRepo = () => doLeave('/join-repo');

  return (
    <header className="repos-header">
      <div className="logo-container">
        <img src="/logo.svg" alt="App Logo" className="app-logo" />
      </div>

      <div className="header-buttons">

        <button
          className="icon-btn theme-btn"
          title={darkMode ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
          onClick={toggleTheme}
        >
          {darkMode ? <FaSun size={20}/> : <FaMoon size={20}/>}
        </button>


        <button
          className="icon-btn"
          title="Home"
          onClick={handleHome}
        >
          <FaHome size={20}/>
        </button>

        <button
          className="icon-btn"
          title="Leave Repo"
          onClick={handleLeaveRepo}
        >
          <FaDoorOpen size={20}/>
        </button>

        <button
          className="icon-btn"
          title="Log Out"
          onClick={handleLogout}
        >
          <FaSignOutAlt size={20}/>
        </button>
      </div>
    </header>
  );
};

export default Header;

