# GitHub Actions Configuration

This directory contains GitHub Actions workflows for the Finance Tracker project.

## Workflows

### 1. Basic Checks (`basic-checks.yml`)
- **Trigger**: Push/PR to main or dev branches
- **Purpose**: Validates project structure and basic file integrity
- **Checks**:
  - Project directory structure
  - Required files existence
  - Docker Compose validation
  - Maven project validation
  - Node.js dependencies validation
  - Markdown and YAML linting

### 2. CI Pipeline (`ci.yml`)
- **Trigger**: Push/PR to main or dev branches
- **Purpose**: Comprehensive testing and building
- **Jobs**:
  - **Backend Tests**: Runs Spring Boot tests with PostgreSQL
  - **Backend Build**: Builds JAR artifact
  - **Frontend Lint & Type Check**: ESLint and TypeScript validation
  - **Frontend Build**: Builds Next.js application
  - **Security Audit**: OWASP dependency check and npm audit
  - **Docker Build**: Tests Docker image building (main/dev only)

### 3. Code Quality (`code-quality.yml`)
- **Trigger**: Push/PR to main or dev branches
- **Purpose**: Code quality analysis and standards enforcement
- **Features**:
  - SonarCloud integration (requires SONAR_TOKEN secret)
  - JaCoCo test coverage reporting
  - ESLint detailed analysis
  - Prettier formatting checks
  - Dependency security review

### 4. Pull Request Checks (`pr-checks.yml`)
- **Trigger**: Pull requests to main or dev branches
- **Purpose**: Comprehensive PR validation
- **Features**:
  - Smart change detection (only runs relevant checks)
  - Detailed PR comments with results
  - Integration testing
  - Test coverage validation
  - Code formatting enforcement

### 5. Deploy (`deploy.yml`)
- **Trigger**: Push to main branch or manual dispatch
- **Purpose**: Build and deploy application
- **Features**:
  - Builds Docker images for backend and frontend
  - Pushes to GitHub Container Registry
  - Staging deployment
  - Production deployment (with manual approval)

## Required Secrets

To use all features, add these secrets to your repository settings:

1. **SONAR_TOKEN**: For SonarCloud integration
   - Go to [SonarCloud.io](https://sonarcloud.io)
   - Create a new project
   - Generate a token and add it to GitHub secrets

## Environment Configuration

The workflows use these environments (create in repository settings):
- `staging`: For staging deployments
- `production`: For production deployments (with protection rules)

## Status Badges

Add these to your README.md:

```markdown
[![CI Pipeline](https://github.com/abhijeet114/finance-tracker/actions/workflows/ci.yml/badge.svg)](https://github.com/abhijeet114/finance-tracker/actions/workflows/ci.yml)
[![Code Quality](https://github.com/abhijeet114/finance-tracker/actions/workflows/code-quality.yml/badge.svg)](https://github.com/abhijeet114/finance-tracker/actions/workflows/code-quality.yml)
[![Deploy](https://github.com/abhijeet114/finance-tracker/actions/workflows/deploy.yml/badge.svg)](https://github.com/abhijeet114/finance-tracker/actions/workflows/deploy.yml)
```

## Local Development

To run similar checks locally:

### Backend
```bash
cd backend
./mvnw clean test                    # Run tests
./mvnw jacoco:report                # Generate coverage
./mvnw spotless:apply               # Format code
./mvnw dependency-check:check       # Security scan
```

### Frontend
```bash
cd frontend
npm ci                              # Install dependencies
npm run lint                       # Run ESLint
npm run lint:fix                   # Fix ESLint issues
npx prettier --write "src/**/*"    # Format code
npm audit                          # Security audit
npm run build                      # Build application
```

## Customization

### Adding New Checks
1. Modify existing workflow files in `.github/workflows/`
2. Add new jobs or steps as needed
3. Update this documentation

### Modifying Triggers
Change the `on:` section in workflow files to modify when they run.

### Adding Environments
1. Go to repository Settings > Environments
2. Create new environment
3. Add protection rules as needed
4. Update deploy.yml to use the new environment
