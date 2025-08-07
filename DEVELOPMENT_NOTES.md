# Development Notes

## Frontend Development Guidelines

### Folder Structure
- **Main frontend code**: Use `/frontend/` folder for all production frontend changes
- **Reference/Demo code**: Use `/demo/` folder as reference for structure, components, and examples
- **Pattern**: When implementing frontend features, check `/demo/` folder first for existing patterns and components that can be adapted

### Key Points
- Frontend changes should be made in `frontend/` directory
- `demo/` directory contains reference implementations and examples
- Always check demo folder for existing patterns before creating new components
- Maintain consistency between demo patterns and frontend implementation

## Authentication System Updates

### Backend Integration
- **API Base URL**: Updated to `http://localhost:8080` for backend connection
- **Auth Endpoints**: 
  - Login: `POST /api/auth/login`
  - Register: `POST /api/auth/register`
  - User Profile: `GET /api/user/profile`

### Frontend Auth Components Updated
1. **AuthContext.tsx**: Custom authentication context replacing NextAuth
2. **authApi.ts**: API functions for login, register, logout, user profile
3. **AuthJsCredentialsSignInForm.tsx**: Login form connected to backend
4. **AuthJsCredentialsSignUpForm.tsx**: Registration form connected to backend
5. **useUser.tsx**: Updated to use custom auth context
6. **AuthGuardRedirect.tsx**: Updated to use custom auth context
7. **layout.tsx**: Replaced SessionProvider with AuthProvider

### Authentication Flow
1. User submits login/signup form
2. Frontend calls backend API endpoints
3. Backend returns JWT token and user info
4. Token stored in localStorage and added to API headers
5. User state managed through AuthContext
6. Protected routes use AuthGuardRedirect

### Usage
- Use `useAuth()` hook for authentication state
- Use `useUser()` hook for user data and operations
- Forms handle token storage and API header management automatically
- Logout clears token and redirects to sign-in

### Testing Authentication
1. **Frontend**: Running on `http://localhost:3002` (updated)
2. **Backend**: Running on `http://localhost:8080`
3. **Test User**: `testuser` / `password123` (working via curl)

### Current Status
✅ Backend authentication endpoints working  
✅ Frontend forms updated with backend integration  
✅ API configuration pointing to correct backend  
✅ Debug logging added to forms and API calls  
✅ NextAuth conflicts resolved - disabled NextAuth routes  
🔧 **Next**: Test login via frontend UI at `http://localhost:3002`

### Debugging Steps
1. Open browser dev tools (F12)
2. Go to Console tab to see debug logs
3. Try logging in with test credentials
4. Check Network tab for API calls
5. Verify localStorage for token storage

---
*This file serves as a quick reference for development patterns and should be updated as the project evolves.*
