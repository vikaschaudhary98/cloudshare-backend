import { useRef } from "react";
import { Upload, X, FileText } from "lucide-react";

const DashboardUpload = ({
  files,
  onFileChange,
  onUpload,
  uploading,
  onRemoveFile,
  remainingUploads,
}) => {
  const inputRef = useRef(null);
  const MAX_FILES = 5;

  const handleDrop = (e) => {
    e.preventDefault();
    const droppedFiles = Array.from(e.dataTransfer.files);
    const syntheticEvent = { target: { files: droppedFiles } };
    onFileChange(syntheticEvent);
  };

  const handleDragOver = (e) => {
    e.preventDefault();
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
      {/* Header */}
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <Upload size={16} className="text-purple-600" />
          <h2 className="text-sm font-semibold text-gray-800">Upload Files</h2>
        </div>
        <span className="text-xs text-gray-400">
          {remainingUploads} of {MAX_FILES} files remaining
        </span>
      </div>

      {/* Drop Zone */}
      <div
        className="border-2 border-dashed border-gray-200 rounded-lg p-10 text-center cursor-pointer hover:border-purple-400 hover:bg-purple-50 transition-all duration-200"
        onClick={() => inputRef.current.click()}
        onDrop={handleDrop}
        onDragOver={handleDragOver}
      >
        <Upload size={28} className="mx-auto text-gray-400 mb-3" />
        <p className="text-sm text-gray-500">Drag and drop files here</p>
        <p className="text-xs text-gray-400 mt-1">or click to browse</p>
        <input
          ref={inputRef}
          type="file"
          multiple
          className="hidden"
          onChange={onFileChange}
        />
      </div>

      {/* Selected Files List */}
      {files.length > 0 && (
        <ul className="mt-4 space-y-2">
          {files.map((file, index) => (
            <li
              key={index}
              className="flex items-center justify-between bg-gray-50 rounded-lg px-3 py-2"
            >
              <div className="flex items-center gap-2 min-w-0">
                <FileText size={14} className="text-purple-500 shrink-0" />
                <span className="text-xs text-gray-700 truncate">{file.name}</span>
              </div>
              <button
                onClick={() => onRemoveFile(index)}
                className="ml-2 shrink-0 text-gray-400 hover:text-red-500 transition"
              >
                <X size={14} />
              </button>
            </li>
          ))}
        </ul>
      )}

      {/* Upload Button */}
      <button
        onClick={onUpload}
        disabled={uploading || files.length === 0}
        className="mt-4 w-full bg-purple-600 text-white text-sm font-medium py-2.5 rounded-lg hover:bg-purple-700 disabled:opacity-40 disabled:cursor-not-allowed transition"
      >
        {uploading ? "Uploading..." : "Upload"}
      </button>
    </div>
  );
};

export default DashboardUpload;
