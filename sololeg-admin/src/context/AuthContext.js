import React, { createContext, useContext, useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';
import api from '../services/api';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [accessToken, setAccessToken] = useState(localStorage.getItem('accessToken'));
    const [refreshToken, setRefreshToken] = useState(localStorage.getItem('refreshToken'));
    const [user, setUser] = useState(() => {
        try {
            return accessToken ? jwtDecode(accessToken) : null;
        } catch {
            return null;
        }
    });

    const login = async (email, password) => {
        const res = await api.post('/auth/login', { email, password });
        const { token, refreshToken } = res.data;

        localStorage.setItem('accessToken', token);
        localStorage.setItem('refreshToken', refreshToken);

        setAccessToken(token);
        setRefreshToken(refreshToken);
        setUser(jwtDecode(token));
    };

    const logout = () => {
        localStorage.clear();
        setAccessToken(null);
        setRefreshToken(null);
        setUser(null);
    };

    useEffect(() => {
        if (!accessToken && refreshToken) {
            api.post('/auth/refresh', { refreshToken })
                .then(res => {
                    const newToken = res.data.accessToken;
                    setAccessToken(newToken);
                    setUser(jwtDecode(newToken));
                    localStorage.setItem('accessToken', newToken);
                })
                .catch(() => logout());
        }
    }, []);

    return (
        <AuthContext.Provider value={{ token: accessToken, user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
