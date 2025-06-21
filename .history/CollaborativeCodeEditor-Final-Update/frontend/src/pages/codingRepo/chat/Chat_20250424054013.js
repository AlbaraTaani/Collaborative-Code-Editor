import React, { useState } from 'react';
import '../CodingPage.css';

const ChatComponent = ({ messages, user, currentFile, role, sendMessage, repo }) => {
    const [chatInput, setChatInput] = useState('');

    const handleSendMessage = () => {
        if (chatInput.trim()) {
            const chatMessage = {
                content: chatInput,
                sender: user.name,
                filename: currentFile?.filename,
                repoId: repo.repoId,
                projectName: currentFile?.projectName,
                role: role,
                type: 'message'
            };
            sendMessage(chatMessage);
            setChatInput('');
        }
    };

    return (
        <div className="chat-container">
            {currentFile?.filename && (
                <div className="file-info-container">
                    <div className="file-info">
                        <strong>File Name:</strong> {currentFile.filename}{currentFile.extension} <br/>
                        <strong>Project:</strong> {currentFile.projectName} <br/>
                        <strong>Created At:</strong> {new Date(currentFile.createdAt).toLocaleString()} <br/>
                        <strong>Last Modified At:</strong> {new Date(currentFile.lastModifiedAt).toLocaleString()} <br/>
                    </div>
                </div>
            )}

            <div className="chat-messages">
                {messages.map((message, index) => (
                    <div key={index} className={`chat-message ${message.type === 'message' ? '' : 'system'}`}>
                        <div className={`message-bubble ${message.sender === user.name ? 'sent' : 'received'}`}>
                            <strong>{message.sender} :</strong><br/> {message.content}

                        </div>
                    </div>
                ))}
            </div>

            <div className="chat-input-container">
                <input
                    type="text"
                    value={chatInput}
                    onChange={(e) => setChatInput(e.target.value)}
                    className="chat-input"
                />
                <button onClick={handleSendMessage} className="send-button">Send</button>
            </div>
        </div>
    );
};

export default ChatComponent;
