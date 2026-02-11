import { useDispatch, useSelector } from "react-redux";
import { setEmail, setName, setPassword } from "./user/UserSlice";

function Home() {
  const user = useSelector((state) => state.user);
  const dispatch = useDispatch();

  return (
    <>
      Marksheet Management System
      {JSON.stringify(user)}
      <label>Name:</label>
      <input type="text" value={user.name || ''} onChange={(e) => {dispatch(setName(e.target.value))}} />
      <label>Email:</label>
      <input type="email" value={user.email || ''} onChange={(e) => {dispatch(setEmail(e.target.value))}}/>
      <label>Password:</label>
      <input type="password" value={user.password || ''} onChange={(e) => {dispatch(setPassword(e.target.value))}}/>
    </>
  );
}

export default Home;