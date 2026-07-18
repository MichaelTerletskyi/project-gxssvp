import { useState, type FormEvent } from 'react';
import { Link } from 'react-router';
import { useRegister } from '../hooks/useAuth';
import type {RegisterRequest} from '../api/auth.types.ts';

export function RegisterPage() {
    const [formData, setFormData] = useState<RegisterRequest>({
        username: '',
        email: '',
        password: '',
    });
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    const registerMutation = useRegister();

    const onSubmit = (e: FormEvent) => {
        e.preventDefault();
        setErrorMessage(null);

        registerMutation.mutate(formData, {
            onError: (err: any) =>
                setErrorMessage(
                    err?.response?.data?.message ?? 'Sing up error!'
                ),
        });
    };

    return (
        <div className="auth-page">
            <h1>Registration</h1>

            <form onSubmit={onSubmit}>
                <label>
                    Username
                    <input
                        type="text"
                        value={formData.username}
                        onChange={(e) =>
                            setFormData({ ...formData, username: e.target.value })
                        }
                        required
                    />
                </label>

                <label>
                    Email
                    <input
                        type="email"
                        value={formData.email}
                        onChange={(e) =>
                            setFormData({ ...formData, email: e.target.value })
                        }
                        required
                    />
                </label>

                <label>
                    Password
                    <input
                        type="password"
                        value={formData.password}
                        onChange={(e) =>
                            setFormData({ ...formData, password: e.target.value })
                        }
                        required
                    />
                </label>

                {errorMessage && <p className="error">{errorMessage}</p>}

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