'use client';

import React, { createContext, useContext, useEffect, useState } from 'react';
import { authLogout, authGetUserProfile } from './authApi';
import { User } from './user';

interface AuthContextType {
	user: User | null;
	isAuthenticated: boolean;
	isLoading: boolean;
	login: (token: string, userInfo: any) => void;
	logout: () => void;
	refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
	const [user, setUser] = useState<User | null>(null);
	const [isLoading, setIsLoading] = useState(true);

	const isAuthenticated = !!user;

	const login = (token: string, userInfo: any) => {
		localStorage.setItem('authToken', token);
		localStorage.setItem('userInfo', JSON.stringify(userInfo));
		
		setUser({
			id: userInfo.id || userInfo.username,
			displayName: userInfo.username,
			email: userInfo.email,
			role: 'USER',
			photoURL: '',
			shortcuts: [],
			settings: {},
			loginRedirectUrl: '/'
		});
	};

	const logout = () => {
		authLogout();
		localStorage.removeItem('authToken');
		localStorage.removeItem('userInfo');
		setUser(null);
	};

	const refreshUser = async () => {
		try {
			const userProfile = await authGetUserProfile();
			setUser(userProfile);
		} catch (error) {
			console.error('Failed to refresh user:', error);
			logout();
		}
	};

	useEffect(() => {
		const initAuth = async () => {
			// Only run on client side
			if (typeof window === 'undefined') {
				setIsLoading(false);
				return;
			}

			try {
				const token = localStorage.getItem('authToken');
				const userInfo = localStorage.getItem('userInfo');

				if (token && userInfo) {
					const parsedUserInfo = JSON.parse(userInfo);
					
					// Set global headers for API calls
					const { setGlobalHeaders } = await import('@/utils/api');
					setGlobalHeaders({
						'Authorization': `Bearer ${token}`
					});

					// Try to refresh user data from backend
					try {
						await refreshUser();
					} catch (error) {
						console.error('Failed to refresh user data, using stored info:', error);
						// Fallback to stored user info if backend call fails
						login(token, parsedUserInfo);
					}
				}
			} catch (error) {
				console.error('Auth initialization failed:', error);
				logout();
			} finally {
				setIsLoading(false);
			}
		};

		initAuth();
	}, []);

	return (
		<AuthContext.Provider
			value={{
				user,
				isAuthenticated,
				isLoading,
				login,
				logout,
				refreshUser
			}}
		>
			{children}
		</AuthContext.Provider>
	);
}

export function useAuth() {
	const context = useContext(AuthContext);
	if (context === undefined) {
		throw new Error('useAuth must be used within an AuthProvider');
	}
	return context;
}
