import { useState, type FormEvent } from 'react';
import { Link } from 'react-router';
import { useLogin } from '../../../hooks/useAuth.ts';
import type { LoginRequest } from '../../../api/auth.types.ts';
import '../../Auth/Auth.css';

type FieldErrors = Partial<Record<keyof LoginRequest, string>>;

interface ApiErrorResponse {
    success: false;
    message: string;
    data?: Record<string, string> | null;
    timestamp: string;
}

export function LoginPage() {
    const [formData, setFormData] = useState<LoginRequest>({
        username: '',
        password: '',
    });
    const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
    const [formError, setFormError] = useState<string | null>(null);
    const loginMutation = useLogin();

    const updateField = (field: keyof LoginRequest, value: string) => {
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

        loginMutation.mutate(formData, {
            onError: (err: any) => {
                const apiError: ApiErrorResponse | undefined = err?.response?.data;
                if (apiError?.data && typeof apiError.data === 'object') {
                    // field-level validation errors (e.g. empty username)
                    setFieldErrors(apiError.data as FieldErrors);
                    setFormError(apiError.message ?? null);
                } else if (err?.response?.status === 401) {
                    // wrong credentials shouldn't reveal which field is wrong
                    setFormError('Incorrect login or password');
                } else {
                    setFormError(apiError?.message ?? 'Sign in error!');
                }
            },
        });
    };

    return (
        <div className="auth-page">
            <div className="auth-card">
                <span className="auth-mascot" aria-hidden="true">(=^-ω-^=)</span>
                <h1 className="auth-title">
                    Sign in<span className="cursor">_</span>
                </h1>
                <form className="auth-form" onSubmit={onSubmit} noValidate>
                    <label className="field-label">
                        <span className="label-text">Username</span>
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
                    <label className="field-label">
                        <span className="label-text">Password</span>
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
                    {formError && <p className="form-error">{formError}</p>}
                    <button className="auth-submit" type="submit" disabled={loginMutation.isPending}>
                        {loginMutation.isPending ? 'Enter...' : 'Enter'}
                    </button>
                </form>
                <p className="auth-footer">
                    Have no account yet? <Link to="/register">Sign up</Link>
                </p>
            </div>
        </div>
    );
}
