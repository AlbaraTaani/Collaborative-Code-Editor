import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import "./CreateRepo.css";

const RepoCreationFlow = () => {
  const [repoName, setRepoName] = useState("");
  const [repoId, setRepoId] = useState("");
  const [newViewer, setNewViewer] = useState("");
  const [newCollaborator, setNewCollaborator] = useState("");
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [showMemberForm, setShowMemberForm] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = location.state || {};

  const handleCreateRepo = async () => {
    setLoading(true);
    setErrorMessage("");
    try {
      const response = await fetch(
        "http://localhost:8080/api/repos/createRepo",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
          body: JSON.stringify({ repoName, memberEmail: user.name }),
        }
      );
      if (!response.ok) {
        let errText = await response.text().catch(() => "");
        throw new Error(errText || "Invalid Repo Name or ID");
      }

      let data = {};
      // only parse JSON if there is content
      const contentLength = response.headers.get("content-length");
      if (response.status !== 204 && contentLength !== "0") {
        data = await response.json();
      } else {
        // no body returned
        throw new Error("No data returned from server");
      }

      setRepoId(data.repoId);
      setShowMemberForm(true);
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setLoading(false);
    }
  };

  const addMember = async (type, member) => {
    setLoading(true);
    setErrorMessage("");
    try {
      const endpoint = type === "viewer" ? "VIEWER" : "COLLABORATOR";
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
            repoId: repoId,
            role: endpoint,
          }),
        }
      );
      if (!response.ok) {
        throw new Error("Failed to add new member");
      }

      type === "viewer" ? setNewViewer("") : setNewCollaborator("");
    } catch (error) {
      setErrorMessage(error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAddViewer = () => addMember("viewer", newViewer);
  const handleAddCollaborator = () =>
    addMember("collaborator", newCollaborator);

  return (
    <div className="form-wrapper">
      {errorMessage && <div className="error-message">{errorMessage}</div>}

      {!showMemberForm ? (
        <form className="creation-form" onSubmit={(e) => e.preventDefault()}>
          <h1 className="form-repo-header">Create Your Repo</h1>
          <input
            className="form-input"
            type="text"
            placeholder="Repo Name"
            value={repoName}
            onChange={(e) => setRepoName(e.target.value)}
            disabled={loading}
          />
          <button
            className="form-action-button"
            type="submit"
            onClick={handleCreateRepo}
            disabled={loading || !repoName}
          >
            {loading ? "Creating..." : "Next"}
          </button>
        </form>
      ) : (
        <div className="member-forms">
          <h1 className="add-member-headerForm">Add Members to {repoName}</h1>
          <input
            className="form-input"
            type="text"
            placeholder="Add Viewer"
            value={newViewer}
            onChange={(e) => setNewViewer(e.target.value)}
            disabled={loading}
          />
          <button
            className="form-action-button"
            onClick={handleAddViewer}
            disabled={loading || !newViewer}
          >
            {loading ? "Adding..." : "Add Viewer"}
          </button>
          <input
            className="form-input"
            type="text"
            placeholder="Add Collaborator"
            value={newCollaborator}
            onChange={(e) => setNewCollaborator(e.target.value)}
            disabled={loading}
          />
          <button
            className="form-action-button"
            onClick={handleAddCollaborator}
            disabled={loading || !newCollaborator}
          >
            {loading ? "Adding..." : "Add Collaborator"}
          </button>
          <button
            className="form-action-button finish-button"
            onClick={() => navigate("/home")}
            disabled={loading}
          >
            {loading ? "Finishing..." : "Finish"}
          </button>
        </div>
      )}
    </div>
  );
};

export default RepoCreationFlow;
