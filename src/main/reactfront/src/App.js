import './App.css';
import axios from 'axios';
import { useEffect, useState } from 'react';

axios.defaults.baseURL = "http://localhost:3000";
axios.defaults.withCredentials = true;

function App() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [token, setToken] = useState(null);
  const [error, setError] = useState(null);
  const history = useHistory();


  const handleLogin = async (event) => {
    event.preventDefault(); // Prevent default form submission behavior
    try {
      const response = await axios.post("http://localhost:8080/login", {
        username,
        password,
      });
      const auth = response.headers.authorization;
      setToken(auth);
      setError(null);
      console.log(response);
      history.push('/');
    } catch (error) {
      setError("Invalid credentials. Please try again.");
    }
  };

  useEffect(() => {
    console.log(token);
    axios.defaults.headers.common['Authorization'] = token;
    // axios.defaults.headers.common["Authorization"] = token;
  }, [token]);

  return (
    <div className="App">
      <header className="App-header">
        <h2>Login Form</h2>
        {error && <p style={{ color: 'red' }}>{error}</p>}
        <form onSubmit={handleLogin}>
          <div>
            <label>Username:</label>
            <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} />
          </div>
          <div>
            <label>Password:</label>
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
          </div>
          <button type="submit">Login</button>
        </form>
      </header>
    </div>
  );
}

export default App