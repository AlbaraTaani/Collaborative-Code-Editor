
// import React from 'react';
// import {useNavigate} from "react-router-dom";

// const Header = ({ user, darkMode, toggleTheme, closeWebSocketConnection, sendChatMessage, repo, role }) => {
//     const navigate = useNavigate();

//     const leaveMessage = {
//         sender: user.name,
//         role: role,
//         filename: '',
//         repoId: repo.repoId,
//         projectName: '',
//         content: 'left the repo',
//         type: 'leave'
//     };

//     const handleLogout = () => {
//         sendChatMessage(leaveMessage);
//         closeWebSocketConnection();
//         localStorage.removeItem('token');
//         navigate('/');
//     };

//     const handleHome = () => {
//         sendChatMessage(leaveMessage);
//         closeWebSocketConnection();
//         navigate('/Home');
//     };
//     const handleLeaveRepo = () => {
//         sendChatMessage(leaveMessage);
//         closeWebSocketConnection();
//         navigate('/join-repo', {
//             state: { user },
//         });
//     };

//     return (
//         <header className="repos-header">
//             <div className="user-info">
//                 <img className="user-image" src={user.profileImage} alt="User"/>
//                 <span className="user-name">{user.name}</span>
//             </div>
//             <div className="header-buttons">
//                 <button className="theme-toggle-btn" onClick={toggleTheme}>
//                     {darkMode ? 'Switch to Dark Mode' : 'Switch to Light Mode'}
//                 </button>
//                 <button className="theme-toggle-btn" onClick={handleHome}>HOME</button>
//                 <button className="logout-btn" onClick={handleLeaveRepo}>LEAVE ROOM</button>
//                 <button className="logout-btn" onClick={handleLogout}>LOG OUT</button>
//             </div>
//         </header>
//     );
// };

// export default Header;
import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  FaHome,
  FaSignOutAlt,
  FaDoorOpen,
  FaMoon,
  FaSun,
} from 'react-icons/fa';
import './Header.css';  // make sure to style .icon-btn, .app-logo, etc.

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
      {/* App logo on the left */}
      <div className="logo-container">
        <img src="/logo.svg" alt="App Logo" className="app-logo" />
      </div>

      <div className="header-buttons">
        {/* Theme toggle */}
        <button
          className="icon-btn theme-btn"
          title={darkMode ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
          onClick={toggleTheme}
        >
          {darkMode ? <FaSun size={20}/> : <FaMoon size={20}/>}
        </button>

        {/* Home */}
        <button
          className="icon-btn"
          title="Home"
          onClick={handleHome}
        >
          <FaHome size={20}/>
        </button>

        {/* Leave Repo */}
        <button
          className="icon-btn"
          title="Leave Repo"
          onClick={handleLeaveRepo}
        >
          <FaDoorOpen size={20}/>
        </button>

        {/* Log Out */}
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

