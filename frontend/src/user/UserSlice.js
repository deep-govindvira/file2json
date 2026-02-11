import { createSlice } from '@reduxjs/toolkit';

export const userSlice = createSlice({
  name: 'user',
  initialState: {
    name: localStorage.getItem("name") || "",
    email: localStorage.getItem("email") || "",
    password: localStorage.getItem("password") || ""
  },
  reducers: {
    setName: (state, action) => { state.name = action.payload; localStorage.setItem("name", action.payload); },
    setEmail: (state, action) => { state.email = action.payload; localStorage.setItem("email", action.payload); },
    setPassword: (state, action) => { state.password = action.payload; localStorage.setItem("password", action.payload); }
  },
});

export const { setName, setEmail, setPassword } = userSlice.actions;
export default userSlice.reducer;
