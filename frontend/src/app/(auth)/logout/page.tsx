'use client';

import { useEffect } from 'react';
import { useAuth } from '@auth/AuthContext';
import { useRouter } from 'next/navigation';
import FuseLoading from '@fuse/core/FuseLoading';

function LogoutPage() {
	const { logout } = useAuth();
	const router = useRouter();

	useEffect(() => {
		logout();
		router.push('/sign-in');
	}, [logout, router]);

	return <FuseLoading />;
}

export default LogoutPage;
