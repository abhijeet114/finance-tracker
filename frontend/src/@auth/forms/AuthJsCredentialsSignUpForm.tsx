import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useState } from 'react';
import { z } from 'zod';
import _ from 'lodash';
import TextField from '@mui/material/TextField';
import FormControl from '@mui/material/FormControl';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import Button from '@mui/material/Button';
import FormHelperText from '@mui/material/FormHelperText';
import { Alert } from '@mui/material';
import { authRegister, RegisterRequest } from '@auth/authApi';
import { useRouter } from 'next/navigation';

/**
 * Form Validation Schema
 */
const schema = z
	.object({
		username: z.string().nonempty('You must enter a username'),
		email: z.string().email('You must enter a valid email').nonempty('You must enter an email'),
		password: z
			.string()
			.nonempty('Please enter your password.')
			.min(8, 'Password is too short - should be 8 chars minimum.'),
		passwordConfirm: z.string().nonempty('Password confirmation is required'),
		acceptTermsConditions: z.boolean().refine((val) => val === true, 'The terms and conditions must be accepted.')
	})
	.refine((data) => data.password === data.passwordConfirm, {
		message: 'Passwords must match',
		path: ['passwordConfirm']
	});

const defaultValues = {
	username: '',
	email: '',
	password: '',
	passwordConfirm: '',
	acceptTermsConditions: false
};

export type FormType = {
	username: string;
	password: string;
	email: string;
};

function AuthJsCredentialsSignUpForm() {
	const router = useRouter();
	const [isLoading, setIsLoading] = useState(false);
	const { control, formState, handleSubmit, setError } = useForm({
		mode: 'onChange',
		defaultValues,
		resolver: zodResolver(schema)
	});

	const { isValid, dirtyFields, errors } = formState;

	async function onSubmit(formData: FormType & { passwordConfirm: string; acceptTermsConditions: boolean }) {
		setIsLoading(true);
		try {
			const { username, email, password } = formData;
			
			const registerData: RegisterRequest = {
				username,
				email,
				password
			};

			const response = await authRegister(registerData);
			
			if (response.token) {
				// Store user data in localStorage
				localStorage.setItem('authToken', response.token);
				localStorage.setItem('userInfo', JSON.stringify({
					username: response.username,
					email: response.email
				}));
				
				// Redirect to dashboard or home page
				router.push('/');
				return true;
			}
		} catch (error: any) {
			console.error('Registration error:', error);
			let errorMessage = 'Registration failed. Please try again.';
			
			// Handle specific error cases
			if (error.response?.status === 400) {
				errorMessage = 'Username or email already exists. Please choose different ones.';
			} else if (error.message) {
				errorMessage = error.message;
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
			name="registerForm"
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
				name="email"
				control={control}
				render={({ field }) => (
					<TextField
						{...field}
						className="mb-6"
						label="Email"
						type="email"
						error={!!errors.email}
						helperText={errors?.email?.message}
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
			<Controller
				name="passwordConfirm"
				control={control}
				render={({ field }) => (
					<TextField
						{...field}
						className="mb-6"
						label="Password (Confirm)"
						type="password"
						error={!!errors.passwordConfirm}
						helperText={errors?.passwordConfirm?.message}
						variant="outlined"
						required
						fullWidth
						disabled={isLoading}
					/>
				)}
			/>
			<Controller
				name="acceptTermsConditions"
				control={control}
				render={({ field }) => (
					<FormControl error={!!errors.acceptTermsConditions}>
						<FormControlLabel
							label="I agree with Terms and Privacy Policy"
							control={
								<Checkbox
									size="small"
									{...field}
									disabled={isLoading}
								/>
							}
						/>
						<FormHelperText>{errors?.acceptTermsConditions?.message}</FormHelperText>
					</FormControl>
				)}
			/>
			<Button
				variant="contained"
				color="secondary"
				className="mt-6 w-full"
				aria-label="Register"
				disabled={_.isEmpty(dirtyFields) || !isValid || isLoading}
				type="submit"
				size="large"
			>
				{isLoading ? 'Creating account...' : 'Create your free account'}
			</Button>
		</form>
	);
}

export default AuthJsCredentialsSignUpForm;
