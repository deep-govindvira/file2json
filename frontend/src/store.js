import { configureStore } from '@reduxjs/toolkit';
import { userIdSlice } from './reducers/userIdSlice';

export const store = configureStore({
  reducer: { userId: userIdSlice.reducer }
});