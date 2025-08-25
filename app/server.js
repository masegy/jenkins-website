const express = require('express');
const cors = require('cors');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 8080;

app.use(cors());
app.use(express.json());
app.use(express.static('dist'));

app.get('/api/health', (req, res) => {
    res.json({
        status: 'healthy',
        service: 'DevOps Demo API',
        timestamp: new Date().toISOString(),
        uptime: process.uptime(),
        environment: process.env.NODE_ENV || 'development'
    });
});

app.get('/api/ready', (req, res) => {
    res.json({
        status: 'ready',
        service: 'DevOps Demo API',
        timestamp: new Date().toISOString()
    });
});

app.get('/api/info', (req, res) => {
    res.json({
        name: 'DevOps Demo Application',
        version: '1.0.0',
        features: [
            'Docker containerization',
            'Kubernetes orchestration',
            'CI/CD pipeline',
            'Infrastructure as Code',
            'Auto-scaling',
            'Health monitoring'
        ],
        databases: {
            postgres: process.env.DATABASE_URL ? 'connected' : 'not configured',
            redis: process.env.REDIS_URL ? 'connected' : 'not configured'
        }
    });
});

app.get('/api/metrics', (req, res) => {
    res.json({
        cpu: Math.random() * 100,
        memory: Math.random() * 100,
        requests_per_second: Math.floor(Math.random() * 1000),
        response_time_ms: Math.floor(Math.random() * 100),
        active_connections: Math.floor(Math.random() * 50)
    });
});

app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'dist', 'index.html'));
});

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
    console.log(`Environment: ${process.env.NODE_ENV || 'development'}`);
});