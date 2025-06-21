import React, {useState, useEffect} from 'react';

const CodeMetricsDisplay = ({code, language, onClose}) => {
    const [metrics, setMetrics] = useState({
        lines: 0,
        functions: 0,
        variables: 0,
        cyclomaticComplexity: 0,
    });
    useEffect(() => {
        const fetchMetrics = async () => {
            try {
                const response = await fetch('http://localhost:8080/api/viewer/CodeMetrics', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${localStorage.getItem('token')}`
                    },
                    body: JSON.stringify({code, language}),
                });
                const data = await response.json();
                setMetrics(data.metrics);
            } catch (error) {
                console.error('Error fetching code metrics:', error);
            }
        };

        if (code) {
            fetchMetrics();
        }
    }, [code, language]);

    return (
        <div className="code-metrics-container">
            {onClose && (
                <div className="modal">
                    <h3>Code Metrics</h3>
                    <ul className="metrics-list">
                        <li>
                            <span>Line Count:</span> {metrics.lines}
                        </li>
                        <li>
                            <span>Function Count:</span> {metrics.functions}
                        </li>
                        <li>
                            <span>Variable Count:</span> {metrics.variables}
                        </li>
                        <li>
                            <span>Cyclomatic Complexity:</span> {metrics.cyclomaticComplexity}
                        </li>
                    </ul>
                    <button className="close-btn" onClick={onClose}>Close</button>
                </div>
            )}
            <div className="modal-backdrop"></div>
        </div>
    );
};

export default CodeMetricsDisplay;

