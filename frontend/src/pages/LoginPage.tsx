import { useState, type FormEvent } from 'react';
import { Link } from 'react-router';
import { useLogin } from '../hooks/useAuth';
import type {LoginRequest} from '../api/auth.types.ts';

export function LoginPage() {
    const [formData, setFormData] = useState<LoginRequest>({
        username: '',
        password: '',
    });
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    const loginMutation = useLogin();

    const onSubmit = (e: FormEvent) => {
        e.preventDefault();
        setErrorMessage(null);

        loginMutation.mutate(formData, {
            onError: () => setErrorMessage('Incorrect login or password'),
        });
    };

    return (
        <div className="auth-page">
            <h1>Sign in</h1>

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

                <button type="submit" disabled={loginMutation.isPending}>
                    {loginMutation.isPending ? 'Enter...' : 'Enter'}
                </button>
            </form>

            <p>
                Have no account yet? <Link to="/register">Sign up</Link>
            </p>
        </div>
    );
}