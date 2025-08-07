import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useEffect, useState } from 'react';
import { z } from 'zod';
import _ from 'lodash';
import TextField from '@mui/material/TextField';
import FormControl from '@mui/material/FormControl';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import Link from '@fuse/core/Link';
import Button from '@mui/material/Button';
import { Alert } from '@mui/material';
import { authLogin, LoginRequest } from '@auth/authApi';
import { useRouter } from 'next/navigation';

/**
 * Form Validation Schema
 */
const schema = z.object({
	username: z.string().nonempty('You must enter a username'),
	password: z
		.string()
		.min(4, 'Password is too short - must be at least 4 chars.')
		.nonempty('Please enter your password.'),
	remember: z.boolean().optional()
});

type FormType = z.infer<typeof schema>;

const defaultValues = {
	username: '',
	password: '',
	remember: true
};

function AuthJsCredentialsSignInForm() {
	const router = useRouter();
	const [isLoading, setIsLoading] = useState(false);
	const { control, formState, handleSubmit, setValue, setError } = useForm<FormType>({
		mode: 'onChange',
		defaultValues,
		resolver: zodResolver(schema)
	});

	const { isValid, dirtyFields, errors } = formState;

	useEffect(() => {
		// Remove default values for production
		setValue('username', '', {
			shouldDirty: false,
			shouldValidate: false
		});
		setValue('password', '', {
			shouldDirty: false,
			shouldValidate: false
		});
	}, [setValue]);

	async function onSubmit(formData: FormType) {
		setIsLoading(true);
		try {
			const { username, password } = formData;
			
			if (typeof window !== 'undefined') {
				console.log('Attempting login with:', { username, password: '***' });
			}
			
			const loginData: LoginRequest = {
				username,
				password
			};

			const response = await authLogin(loginData);
			
			if (typeof window !== 'undefined') {
				console.log('Login response:', { 
					hasToken: !!response.token, 
					username: response.username,
					email: response.email 
				});
			}
			
			if (response.token) {
				// Store user data in localStorage or state management
				localStorage.setItem('authToken', response.token);
				localStorage.setItem('userInfo', JSON.stringify({
					username: response.username,
					email: response.email
				}));
				
				if (typeof window !== 'undefined') {
					console.log('Login successful, redirecting to home');
				}
				// Redirect to dashboard or home page
				router.push('/');
				return true;
			} else {
				if (typeof window !== 'undefined') {
					console.error('No token received in response');
				}
				setError('root', { 
					type: 'manual', 
					message: 'Authentication failed - no token received.' 
				});
			}
		} catch (error: unknown) {
			const errorObj = error as any;
			
			if (typeof window !== 'undefined') {
				console.error('Login error details:', {
					error: errorObj,
					message: errorObj?.message,
					response: errorObj?.response,
					status: errorObj?.response?.status
				});
			}
			
			let errorMessage = 'Invalid username or password. Please try again.';
			
			if (errorObj?.response?.status === 400) {
				errorMessage = 'Invalid login credentials.';
			} else if (errorObj?.response?.status === 500) {
				errorMessage = 'Server error. Please try again later.';
			} else if (errorObj?.message?.includes('fetch')) {
				errorMessage = 'Cannot connect to server. Please check if the backend is running.';
			}
			
			setError('root', { 
				type: 'manual', 
				message: errorMessage
			});
		} finally {
			setIsLoading(false);
		}

		return false;
	}

	return (
		<form
			name="loginForm"
			noValidate
			className="flex w-full flex-col justify-center"
			onSubmit={handleSubmit(onSubmit)}
		>
			{errors?.root?.message && (
				<Alert
					className="mb-8"
					severity="error"
					sx={(theme) => ({
						backgroundColor: theme.palette.error.light,
						color: theme.palette.error.dark
					})}
				>
					{errors?.root?.message}
				</Alert>
			)}
			<Controller
				name="username"
				control={control}
				render={({ field }) => (
					<TextField
						{...field}
						className="mb-6"
						label="Username"
						autoFocus
						type="text"
						error={!!errors.username}
						helperText={errors?.username?.message}
						variant="outlined"
						required
						fullWidth
						disabled={isLoading}
					/>
				)}
			/>
			<Controller
				name="password"
				control={control}
				render={({ field }) => (
					<TextField
						{...field}
						className="mb-6"
						label="Password"
						type="password"
						error={!!errors.password}
						helperText={errors?.password?.message}
						variant="outlined"
						required
						fullWidth
						disabled={isLoading}
					/>
				)}
			/>
			<div className="flex flex-col items-center justify-center sm:flex-row sm:justify-between">
				<Controller
					name="remember"
					control={control}
					render={({ field }) => (
						<FormControl>
							<FormControlLabel
								label="Remember me"
								control={
									<Checkbox
										size="small"
										{...field}
										disabled={isLoading}
									/>
								}
							/>
						</FormControl>
					)}
				/>

				<Link
					className="text-md font-medium"
					to="/#"
				>
					Forgot password?
				</Link>
			</div>
			<Button
				variant="contained"
				color="secondary"
				className="mt-4 w-full"
				aria-label="Sign in"
				disabled={_.isEmpty(dirtyFields) || !isValid || isLoading}
				type="submit"
				size="large"
			>
				{isLoading ? 'Signing in...' : 'Sign in'}
			</Button>
		</form>
	);
}

export default AuthJsCredentialsSignInForm;
