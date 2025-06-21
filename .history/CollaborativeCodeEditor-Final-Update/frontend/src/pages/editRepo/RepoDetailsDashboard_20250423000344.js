import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./EditRepo.css";
import { FaSun, FaMoon, FaHome, FaSignOutAlt } from "react-icons/fa";
import logo from "../../logo.svg";
import "./EditRepo.css";

const RepoAdmin = () => {
  const [viewers, setViewers] = useState([]);
  const [collaborators, setCollaborators] = useState([]);
  const [projects, setProjects] = useState([]);
  const [repoName, setRepoName] = useState("");
  const [newViewer, setNewViewer] = useState("");
  const [newName, setNewName] = useState("");
  const [newCollaborator, setNewCollaborator] = useState("");
  const [newProject, setNewProject] = useState(""); // For adding new project
  const [darkMode, setDarkMode] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();
  const { user, repo } = location.state;

  const fetchRepoDetails = async () => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/repos/${repo.repoId}/details`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      if (!response.ok) throw new Error("Failed to fetch repo details");
      const data = await response.json();
      setViewers(data.viewers.flat());
      setCollaborators(data.collaborators.flat());
      setProjects(data.projects.flat());
      setRepoName(repo.name);
    } catch (error) {
      console.error("Error fetching repo details:", error);
    }
  };

  useEffect(() => {
    fetchRepoDetails();
  }, []);

  const addMember = async (type, member) => {
    const endpoint = type === "viewer" ? "VIEWER" : "COLLABORATOR";
    try {
      const response = await fetch(
        `http://localhost:8080/api/repos/add-member`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
          body: JSON.stringify({
            memberEmail: member,
            repoId: repo.repoId,
            role: endpoint,
          }),
        }
      );
      if (!response.ok) throw new Error("Failed to add new member");
      if (type === "viewer") setViewers([...viewers, newViewer]);
      else setCollaborators([...collaborators, newCollaborator]);
      setNewViewer("");
      setNewCollaborator("");
    } catch (error) {
      console.error("Error adding member:", error);
    }
  };

  const handleAddViewer = () => addMember("viewer", newViewer);
  const handleAddCollaborator = () =>
    addMember("collaborator", newCollaborator);

  const removeMember = async (type, member) => {
    try {
      const endpoint = type === "viewer" ? "VIEWER" : "COLLABORATOR";
      const response = await fetch(
        `http://localhost:8080/api/repos/remove-member`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
          body: JSON.stringify({
            repoId: repo.repoId,
            memberEmail: member,
            role: endpoint,
          }),
        }
      );
      if (!response.ok) throw new Error("Failed to remove member");
      if (type === "viewer") setViewers(viewers.filter((v) => v !== member));
      else setCollaborators(collaborators.filter((c) => c !== member));
    } catch (error) {
      console.error("Error removing member:", error);
    }
  };

  const handleRemoveViewer = (viewer) => removeMember("viewer", viewer);
  const handleRemoveCollaborator = (collaborator) =>
    removeMember("collaborator", collaborator);

  const handleRemoveProject = async (project) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/projects/remove-project`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
          body: JSON.stringify({
            projectName: project.projectName,
            repoId: project.repoId,
          }),
        }
      );
      if (!response.ok) throw new Error("Failed to remove project");
      setProjects(projects.filter((p) => p !== project));
    } catch (error) {
      console.error("Error removing project:", error);
    }
  };

  const handleRenameRepo = async (newName) => {
    try {
      const response = await fetch(`http://localhost:8080/api/repos/rename`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
        body: JSON.stringify({ name: newName, repoId: repo.repoId }),
      });
      if (!response.ok) throw new Error("Failed to rename repo");
      setRepoName(newName);
    } catch (error) {
      console.error("Error renaming repo:", error);
    }
  };

  const handleAddProject = async () => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/projects/create-project`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
          body: JSON.stringify({
            projectName: newProject,
            repoId: repo.repoId,
          }),
        }
      );
      if (!response.ok) throw new Error("Failed to add project");
      setProjects([
        ...projects,
        { projectName: newProject, repoId: repo.repoId },
      ]);
      setNewProject("");
    } catch (error) {
      console.error("Error adding project:", error);
    }
  };
  const toggleTheme = () => setDarkMode(!darkMode);

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/");
  };
  const handleHome = () => {
    navigate("/Home");
  };

  return (
    <div
      className={`repo-admin-container ${
        darkMode ? "light-mode" : "dark-mode"
      }`}
    >
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

      <div className="repo-admin-body">
        <h2 className="edit-h2">{repoName}</h2>
        <div className="stats">
        <input
          type="text"
          value={newName}
          onChange={(e) => setNewName(e.target.value)}
          placeholder="New repo name"
        />
        <button
          onClick={() => handleRenameRepo(newName)}
          className="edit-button"
        >
          Rename
        </button>
        </div>
        {/* Statistics Section */}
        {/* <section className="stats">
          <div className="stat-card">
            <h3>Viewers</h3>
            <p>{viewers.length}</p>
          </div>
          <div className="stat-card">
            <h3>Collaborators</h3>
            <p>{collaborators.length}</p>
          </div>
        </section> */}

        {/* Management Sections */}
        <section className="management-section">
          {/* Viewer Management */}
          <div className="viewer-management">
            <h3>Viewers</h3>
            <ul>
              {viewers.map((viewer) => (
                <li key={viewer}>
                  {viewer}
                  <button
                    onClick={() => handleRemoveViewer(viewer)}
                    className="remove-btn"
                  >
                    Remove
                  </button>
                </li>
              ))}
            </ul>
            <input
              type="text"
              value={newViewer}
              onChange={(e) => setNewViewer(e.target.value)}
              placeholder="Add new viewer"
            />
            <button onClick={handleAddViewer} className="edit-button">
              Add Viewer
            </button>
          </div>

          {/* Collaborator Management */}
          <div className="collaborator-management">
            <h3>Collaborators</h3>
            <ul>
              {collaborators.map((collaborator) => (
                <li key={collaborator}>
                  {collaborator}
                  <button
                    onClick={() => handleRemoveCollaborator(collaborator)}
                    className="remove-btn"
                  >
                    Remove
                  </button>
                </li>
              ))}
            </ul>
            <input
              type="text"
              value={newCollaborator}
              onChange={(e) => setNewCollaborator(e.target.value)}
              placeholder="Add new collaborator"
            />
            <button onClick={handleAddCollaborator} className="edit-button">
              Add Collaborator
            </button>
          </div>

          {/* new:  */}
          <div className="project-management">
          <h3>Projects</h3>
          <ul>
            {projects.map((project) => (
              <li key={project.projectName}>
                {project.projectName}
                <button
                  onClick={() => handleRemoveProject(project)}
                  className="remove-btn"
                >
                  Remove
                </button>
              </li>
            ))}
          </ul>
          <input
            type="text"
            value={newProject}
            onChange={(e) => setNewProject(e.target.value)}
            placeholder="new branch name"
          />
          <button onClick={handleAddProject} className="edit-button">
            Add Branch
          </button>
        </div>
        </section>
        {/* Project Management */}
{/* 
        <section className="project-management">
          <h3>Branches</h3>
          <ul>
            {projects.map((project) => (
              <li key={project.projectName}>
                {project.projectName}
                <button
                  onClick={() => handleRemoveProject(project)}
                  className="remove-btn"
                >
                  Remove
                </button>
              </li>
            ))}
          </ul>
          <input
            type="text"
            value={newProject}
            onChange={(e) => setNewProject(e.target.value)}
            placeholder="new branch name"
          />
          <button onClick={handleAddProject} className="edit-button">
            Add Branch
          </button>
        </section> */}
      </div>
    </div>
  );
};

export default RepoAdmin;

//     return (
//         <div className={`repo-admin-container ${darkMode ? 'light-mode' : 'dark-mode'}`}>
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

//             <div className="repo-admin-body">
//                 <h2 className="edit-h2" >Manage Repo: {repoName}</h2>
//                 <input
//                     type="text"
//                     value={newName}
//                     onChange={(e) => setNewName(e.target.value)}
//                     placeholder="New repo name"
//                 />
//                 <button onClick={() => handleRenameRepo(newName)} className="edit-button">Rename Repo</button>

//                 <section className="stats">
//                     <div className="stat-card">
//                         <h3>Viewers</h3>
//                         <p>{viewers.length}</p>
//                     </div>
//                     <div className="stat-card">
//                         <h3>Collaborators</h3>
//                         <p>{collaborators.length}</p>
//                     </div>
//                 </section>

//                 <section className="management-section">
//                     <div className="viewer-management">
//                         <h3>Viewers</h3>
//                         <ul>
//                             {viewers.map(viewer => (
//                                 <li key={viewer}>
//                                     {viewer}
//                                     <button onClick={() => handleRemoveViewer(viewer)} className="remove-btn">Remove</button>
//                                 </li>
//                             ))}
//                         </ul>
//                         <input
//                             type="text"
//                             value={newViewer}
//                             onChange={(e) => setNewViewer(e.target.value)}
//                             placeholder="Add new viewer"
//                         />
//                         <button onClick={handleAddViewer} className="edit-button">Add Viewer</button>
//                     </div>

//                     <div className="collaborator-management">
//                         <h3>Collaborators</h3>
//                         <ul>
//                             {collaborators.map(collaborator => (
//                                 <li key={collaborator}>
//                                     {collaborator}
//                                     <button onClick={() => handleRemoveCollaborator(collaborator)} className="remove-btn">Remove</button>
//                                 </li>
//                             ))}
//                         </ul>
//                         <input
//                             type="text"
//                             value={newCollaborator}
//                             onChange={(e) => setNewCollaborator(e.target.value)}
//                             placeholder="Add new collaborator"
//                         />
//                         <button onClick={handleAddCollaborator} className="edit-button">Add Collaborator</button>
//                     </div>
//                 </section>

//                 <section className="project-management">
//                     <h3>Projects</h3>
//                     <ul>
//                         {projects.map(project => (
//                             <li key={project.projectName}>
//                                 {project.projectName}
//                                 <button onClick={() => handleRemoveProject(project)} className="remove-btn">Remove</button>
//                             </li>
//                         ))}
//                     </ul>
//                     <input
//                         type="text"
//                         value={newProject}
//                         onChange={(e) => setNewProject(e.target.value)}
//                         placeholder="Add new project"
//                     />
//                     <button onClick={handleAddProject} className="edit-button">Add Project</button>
//                 </section>
//             </div>
//         </div>
//     );
// };

// export default RepoAdmin;
