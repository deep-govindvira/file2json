import axiosInstance from "./axiosInstance";

export const uploadMarksheets = async (projectId, files, setFiles) => {
    if (!files || !files.length) {
        throw new Error("No files selected for upload");
    }

    const chunkSize = 10;

    // Helper to split files into chunks
    const chunks = [];
    for (let i = 0; i < files.length; i += chunkSize) {
        chunks.push(files.slice(i, i + chunkSize));
    }

    const allResponses = [];

    for (const chunk of chunks) {
        const formData = new FormData();
        chunk.forEach((file) => formData.append("files", file));

        const response = await axiosInstance.post(
            `/projects/${projectId}/marksheets/upload`,
            formData,
            {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
            }
        );

        setFiles((prevFiles) =>
            prevFiles.filter((file) => !chunk.includes(file))
        );


        allResponses.push(response.data);
    }

    return allResponses; // returns array of responses per chunk
};


export const getMarksheets = async (projectId) => {
    const response = await axiosInstance.get(`/projects/${projectId}/marksheets`);
    return response.data;
};

export const getMarksheetById = async (projectId, marksheetId) => {
    const response = await axiosInstance.get(`/projects/${projectId}/marksheets/${marksheetId}`);
    return response.data;
};

export const process = async (projectId) => {
    const response = await axiosInstance.post(`/projects/${projectId}/marksheets/process`);
    return response.data;
}

export const processById = async (projectId, marksheetId) => {
    const response = await axiosInstance.post(`/projects/${projectId}/marksheets/${marksheetId}/process`);
    return response.data;
}

export const updateMarksheet = async (projectId, marksheetId, data) => {
    const response = await axiosInstance.put(`/projects/${projectId}/marksheets/${marksheetId}`, data);
    return response.data;
};
