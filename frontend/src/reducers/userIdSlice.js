import { createSlice } from '@reduxjs/toolkit';

export const userIdSlice = createSlice({
  name: 'userId',
  initialState: { value: localStorage.getItem("userId") || null },
  reducers: {
    setUserId: (state, action) => { state.value = action.payload; localStorage.setItem("userId", action.payload); },
    clearUserId: (state) => { state.value = null; localStorage.removeItem("userId"); },
  },
});

export const { setUserId, clearUserId } = userIdSlice.actions;
export default userIdSlice.reducer;