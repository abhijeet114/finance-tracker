// NextAuth disabled - using custom authentication
export async function GET() {
	return new Response('Authentication disabled', { status: 404 });
}

export async function POST() {
	return new Response('Authentication disabled', { status: 404 });
}
