import { useState, type FormEvent } from 'react';
import { Link } from 'react-router';
import { useRegister } from '../hooks/useAuth';
import type { RegisterRequest } from '../api/auth.types.ts';

type FieldErrors = Partial<Record<keyof RegisterRequest, string>>;

interface ApiErrorResponse {
    success: false;
    message: string;
    data?: Record<string, string> | null;
    timestamp: string;
}

export function RegisterPage() {
    const [formData, setFormData] = useState<RegisterRequest>({
        username: '',
        email: '',
        password: '',
    });
    const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
    const [formError, setFormError] = useState<string | null>(null);
    const registerMutation = useRegister();

    const updateField = (field: keyof RegisterRequest, value: string) => {
        setFormData((prev) => ({ ...prev, [field]: value }));
        setFieldErrors((prev) => {
            if (!prev[field]) return prev;
            const next = { ...prev };
            delete next[field];
            return next;
        });
    };

    const onSubmit = (e: FormEvent) => {
        e.preventDefault();
        setFormError(null);
        setFieldErrors({});

        registerMutation.mutate(formData, {
            onError: (err: any) => {
                const apiError: ApiErrorResponse | undefined = err?.response?.data;

                if (apiError?.data && typeof apiError.data === 'object') {
                    setFieldErrors(apiError.data as FieldErrors);
                    setFormError(apiError.message ?? null);
                } else {
                    setFormError(
                        apiError?.message ?? 'Sign up error!'
                    );
                }
            },
        });
    };

    return (
        <div className="auth-page">
            <h1>Registration</h1>
            <form onSubmit={onSubmit} noValidate>
                <label>
                    Username
                    <input
                        type="text"
                        value={formData.username}
                        onChange={(e) => updateField('username', e.target.value)}
                        aria-invalid={!!fieldErrors.username}
                        required
                    />
                    {fieldErrors.username && (
                        <span className="field-error">{fieldErrors.username}</span>
                    )}
                </label>
                <label>
                    Email
                    <input
                        type="email"
                        value={formData.email}
                        onChange={(e) => updateField('email', e.target.value)}
                        aria-invalid={!!fieldErrors.email}
                        required
                    />
                    {fieldErrors.email && (
                        <span className="field-error">{fieldErrors.email}</span>
                    )}
                </label>
                <label>
                    Password
                    <input
                        type="password"
                        value={formData.password}
                        onChange={(e) => updateField('password', e.target.value)}
                        aria-invalid={!!fieldErrors.password}
                        required
                    />
                    {fieldErrors.password && (
                        <span className="field-error">{fieldErrors.password}</span>
                    )}
                </label>
                {formError && <p className="error">{formError}</p>}
                <button type="submit" disabled={registerMutation.isPending}>
                    {registerMutation.isPending ? 'Register...' : 'Sign up'}
                </button>
            </form>
            <p>
                Already have an account? <Link to="/login">Sign in</Link>
            </p>
        </div>
    );
}