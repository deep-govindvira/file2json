import { Upload, ShieldCheck, FileText } from 'lucide-react';
import PortalCard from './PortalCard';

function Home() {
  return (
    <div className="min-h-screen bg-slate-50 font-sans">
      {/* Navigation */}
      <nav className="flex items-center justify-between px-10 py-6 bg-white border-b border-slate-100">
        <div className="flex items-center gap-2 text-2xl font-bold text-blue-600">
          <FileText /> <span>MarkStream</span>
        </div>
        <button className="text-slate-600 font-medium hover:text-blue-600 transition">
          Sign In
        </button>
      </nav>

      {/* Hero Section */}
      <header className="max-w-4xl mx-auto px-6 py-20 text-center">
        <div className="inline-block px-4 py-1.5 mb-6 text-sm font-semibold tracking-wide text-blue-600 uppercase bg-blue-50 rounded-full">
          New: AI-Powered Extraction
        </div>
        <h1 className="text-5xl font-black text-slate-900 mb-6 tracking-tight">
          Marksheets <span className="text-blue-600">Management System</span>
        </h1>
        <p className="text-xl text-slate-500 leading-relaxed">
          Upload images or PDFs. Our system extracts the data, and admins verify the results. Simple, fast, and secure.
        </p>
      </header>

      {/* <main>
        <button onClick={}></button>
      </main> */}

      {/* Portal Selection */}
      {/* <main className="max-w-6xl mx-auto px-6 pb-24 grid md:grid-cols-2 gap-10">
        <PortalCard 
          title="User Portal"
          description="Submit your documents for verification and track your progress in real-time."
          icon={Upload}
          buttonText="Upload Marksheet"
          features={["PDF & Image Support", "Real-time Status", "Instant Confirmation"]}
        />
        
        <PortalCard 
          title="Admin Dashboard"
          description="Manage the processing queue, validate OCR results, and export verified data."
          icon={ShieldCheck}
          buttonText="Process Queue"
          dark={true}
          features={["OCR Validation Tool", "Bulk Export (CSV/JSON)", "User Management"]}
        />
      </main> */}

      {/* Simple Footer */}
      <footer className="py-12 border-t border-slate-200 text-center text-slate-400 text-sm">
        &copy; 2026 MarkStream AI • Secure Data Processing
      </footer>
    </div>
  );
}

export default Home;