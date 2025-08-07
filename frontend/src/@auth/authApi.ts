import { User } from '@auth/user';
import UserModel from '@auth/user/models/UserModel';
import { PartialDeep } from 'type-fest';
import api, { setGlobalHeaders, removeGlobalHeaders } from '@/utils/api';

// Auth request/response types
export interface LoginRequest {
	username: string;
	password: string;
}

export interface RegisterRequest {
	username: string;
	email: string;
	password: string;
}

export interface AuthResponse {
	token: string;
	type: string;
	username: string;
	email: string;
}

/**
 * Login user
 */
export async function authLogin(loginData: LoginRequest): Promise<AuthResponse> {
	console.log('authLogin called with:', { username: loginData.username, password: '***' });
	console.log('API base URL:', process.env.NODE_ENV === 'development' ? 'http://localhost:8080' : 'production');
	
	try {
		const response = await api.post('auth/login', {
			json: loginData
		}).json<AuthResponse>();
		
		console.log('Login API response:', { 
			hasToken: !!response.token, 
			username: response.username,
			email: response.email 
		});
		
		// Set the token in global headers for future requests
		if (response.token) {
			setGlobalHeaders({
				'Authorization': `Bearer ${response.token}`
			});
		}
		
		return response;
	} catch (error) {
		console.error('authLogin API error:', error);
		throw error;
	}
}

/**
 * Register user
 */
export async function authRegister(registerData: RegisterRequest): Promise<AuthResponse> {
	const response = await api.post('auth/register', {
		json: registerData
	}).json<AuthResponse>();
	
	// Set the token in global headers for future requests
	if (response.token) {
		setGlobalHeaders({
			'Authorization': `Bearer ${response.token}`
		});
	}
	
	return response;
}

/**
 * Logout user
 */
export function authLogout(): void {
	// Remove the authorization header
	removeGlobalHeaders(['Authorization']);
}

/**
 * Get user profile
 */
export async function authGetUserProfile(): Promise<User> {
	const response = await api.get('user/profile').json();
	return UserModel(response);
}

/**
 * Get user by id
 */
export async function authGetDbUser(userId: string): Promise<Response> {
	return api.get(`user/profile`);
}

/**
 * Get user by email (fallback to profile for now)
 */
export async function authGetDbUserByEmail(email: string): Promise<Response> {
	return api.get(`user/profile`);
}

/**
 * Update user
 */
export function authUpdateDbUser(user: PartialDeep<User>) {
	return api.put(`user/profile`, {
		json: UserModel(user)
	});
}

/**
 * Create user (same as register)
 */
export async function authCreateDbUser(user: PartialDeep<User>) {
	const registerData: RegisterRequest = {
		username: user.displayName || '',
		email: user.email || '',
		password: '' // This would need to be provided separately
	};
	return authRegister(registerData);
}
