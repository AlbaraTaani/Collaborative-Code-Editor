import React, { useState, useEffect } from "react";
import {
  BrowserRouter as Router,
  Route,
  Routes,
  useLocation,
} from "react-router-dom";
import { AnimatePresence } from "framer-motion";
import Loading from "./pages/app/Loading";
import Login from "./pages/loginForm/Login";
import Home from "./pages/home/Home";
import CreateRepo from "./pages/createRepo/CreateRepo";
import JoinRepo from "./pages/joinRepo/JoinRepo";
import CodingPage from "./pages/codingRepo/CodingPage";
import OAuth2RedirectHandler from "./pages/app/OAuth2RedirectHandler";
import NotFound from "./pages/app/NotFound";
import EditRepo from "./pages/editRepo/EditRepo";
import RepoDetailsDashboard from "./pages/editRepo/RepoDetailsDashboard";
import Control from "./pages/codingRepo/versionControl/Control";
import HistoryPage from "./pages/codingRepo/versionControl/HistoryPage";

function App() {
  const [loading, setLoading] = useState(true);
  const location = useLocation();
  const [user, setUser] = useState(null);
  const [repo, setRepo] = useState({ repoId: "", repoName: "" });

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const token = localStorage.getItem("token");
        if (!token) return;
        const response = await fetch("http://localhost:8080/api/user/info", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        if (!response.ok) throw new Error("Failed to fetch user");
        const userData = await response.json();
        setUser(userData);
      } catch (error) {
        console.error("Error fetching user:", error);
      }
    };
    fetchUser();
  }, []);

  useEffect(() => {
    setLoading(true);
    const timeout = setTimeout(() => setLoading(false), 1000);
    return () => clearTimeout(timeout);
  }, [location.pathname]);

  return (
    <div className="App">
      <AnimatePresence mode="wait">{loading && <Loading />}</AnimatePresence>

      {!loading && (
        <AnimatePresence mode="wait">
          <Routes location={location} key={location.pathname}>
            LandingPage
            <Route path="/" element={<Login />} />
            <Route path="/login" element={<Login />} />
            <Route path="/home" element={<Home user={user} />} />
            <Route
              path="/create-repo"
              element={<CreateRepo user={user} repo={repo} />}
            />
            <Route path="/join-repo" element={<JoinRepo />} />
            <Route
              path="/join-repo/:repoId/:roleClick"
              element={<CodingPage />}
            />
            <Route path="/edit-repo" element={<EditRepo />} />
            <Route
              path="/edit-repo/repo/:repoId"
              element={<RepoDetailsDashboard />}
            />

     
            <Route
              path="/oauth2/redirect"
              element={<OAuth2RedirectHandler />}
            />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </AnimatePresence>
      )}
    </div>
  );
}

export default function AppWrapper() {
  return (
    <Router>
      <App />
    </Router>
  );
}
