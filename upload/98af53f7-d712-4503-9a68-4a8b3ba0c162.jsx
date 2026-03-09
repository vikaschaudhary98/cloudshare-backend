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

  return (
    <div className="bg-white rounded-2xl border border-gray-200 p-5">
      {/* Header Row */}
      <div className="flex items-center justify-between mb-3">
        <div className="flex items-center gap-2 text-gray-700">
          <Upload size={15} className="text-gray-600" />
          <span className="text-sm font-semibold">Upload Files</span>
        </div>
        <span className="text-xs text-gray-400">
          {remainingUploads} of {MAX_FILES} files remaining
        </span>
      </div>

      {/* Drop Zone */}
      <div
        className="border border-dashed border-gray-300 rounded-xl flex flex-col items-center justify-center py-14 cursor-pointer hover:bg-gray-50 transition-colors"
        onClick={() => inputRef.current.click()}
        onDrop={handleDrop}
        onDragOver={(e) => e.preventDefault()}
      >
        <div className="mb-3 text-gray-400">
          <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
            <polyline points="17 8 12 3 7 8"/>
            <line x1="12" y1="3" x2="12" y2="15"/>
          </svg>
        </div>
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

      {/* Selected Files */}
      {files.length > 0 && (
        <ul className="mt-3 space-y-1.5">
          {files.map((file, index) => (
            <li key={index} className="flex items-center justify-between bg-gray-50 rounded-lg px-3 py-2">
              <div className="flex items-center gap-2 min-w-0">
                <FileText size={13} className="text-purple-500 shrink-0" />
                <span className="text-xs text-gray-600 truncate">{file.name}</span>
              </div>
              <button onClick={() => onRemoveFile(index)} className="ml-2 text-gray-300 hover:text-red-400 transition">
                <X size={13} />
              </button>
            </li>
          ))}
        </ul>
      )}

      {/* Upload Button — purple gradient like tutorial */}
      <button
        onClick={onUpload}
        disabled={uploading || files.length === 0}
        className="mt-4 w-full py-2.5 rounded-lg text-sm font-semibold text-white transition disabled:opacity-40 disabled:cursor-not-allowed"
        style={{ background: "linear-gradient(90deg, #7c3aed, #a855f7)" }}
      >
        {uploading ? "Uploading..." : "Upload"}
      </button>
    </div>
  );
};

export default DashboardUpload;
