import { useCallback } from 'react';

const useChatManager = (wsRef, isConnected, currentFile, setMessages, user, role, repo) => {
    const subscribeToChat = useCallback(() => {

        if (isConnected && wsRef.current) {

            wsRef.current.subscribe(`/topic/chat/${repo.repoId}`, (message) => {
                const chatData = JSON.parse(message.body);
                if (chatData.repoId === repo.repoId) {
                    setMessages((prevMessages) => [...prevMessages, chatData]);
                }

            });
        } else {
            console.log('Cannot subscribe to chat, WebSocket is not connected.');
        }
    }, [isConnected, wsRef, repo, setMessages]);

    const sendChatMessage = useCallback((chatMessage) => {
        if (wsRef.current && isConnected) {

            wsRef.current.publish({
                destination: `/app/chat/${repo.repoId}`,
                body: JSON.stringify(chatMessage),
                headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
            });
        } else {
            console.log('Cannot send message, WebSocket is not connected.');
        }
    }, [wsRef, isConnected, repo]);

    const sendActionMessage = useCallback((actionType) => {
        if (wsRef.current && isConnected) {
            const actionMessage = {
                content: `${user.name} performed a ${actionType} on ${currentFile.filename}`,
                sender: user.name,
                repoId: currentFile.repoId,
                projectName: currentFile.projectName,
                role: role,
                type: actionType,
            };
            sendChatMessage(actionMessage);
        } else {
            console.log('Cannot send action message, WebSocket is not connected.');
        }
    }, [wsRef, isConnected, user, currentFile, role, sendChatMessage]);

    return { subscribeToChat, sendChatMessage, sendActionMessage };
};

export default useChatManager;
