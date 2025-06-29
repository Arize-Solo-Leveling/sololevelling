import React, { useEffect, useState } from 'react';
import api from '../../services/api';

const Dashboard = () => {
    const [stats, setStats] = useState(null);

    useEffect(() => {
        api.get('/admin/stats').then(res => setStats(res.data));
    }, []);

    if (!stats) return <div>Loading stats...</div>;

    return (
        <div className="p-4">
            <h2>Admin Dashboard</h2>
            <ul>
                <li>Users: {stats.totalUsers}</li>
                <li>Quests: {stats.totalQuests}</li>
                <li>Dungeons: {stats.totalDungeons}</li>
            </ul>
        </div>
    );
};

export default Dashboard;
