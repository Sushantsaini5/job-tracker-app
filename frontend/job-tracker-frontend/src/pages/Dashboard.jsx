// src/pages/Dashboard.jsx
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { jobAPI } from '../services/api';
import { 
  PlusCircle, Briefcase, Clock, CheckCircle, 
  XCircle, TrendingUp, Filter, Search 
} from 'lucide-react';
import Layout from '../components/Layout';

const Dashboard = () => {
  const [stats, setStats] = useState(null);
  const [recentJobs, setRecentJobs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [statsRes, jobsRes] = await Promise.all([
        jobAPI.getStats(),
        jobAPI.getAll({ page: 0, size: 5, sortBy: 'createdAt', sortDir: 'desc' })
      ]);
      
      setStats(statsRes.data);
      setRecentJobs(jobsRes.data.content);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    const colors = {
      APPLIED: 'bg-blue-100 text-blue-800',
      SCREENING: 'bg-yellow-100 text-yellow-800',
      INTERVIEW: 'bg-purple-100 text-purple-800',
      OFFER: 'bg-green-100 text-green-800',
      ACCEPTED: 'bg-green-200 text-green-900',
      REJECTED: 'bg-red-100 text-red-800',
      WITHDRAWN: 'bg-gray-100 text-gray-800'
    };
    return colors[status] || 'bg-gray-100 text-gray-800';
  };

  if (loading) {
    return (
      <Layout>
        <div className="flex items-center justify-center h-64">
          <div className="text-gray-600">Loading...</div>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="space-y-6">
        {/* Header */}
        <div className="flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
            <p className="text-gray-600 mt-1">Track your job application journey</p>
          </div>
          <Link
            to="/jobs/new"
            className="bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 transition-colors flex items-center gap-2"
          >
            <PlusCircle className="w-5 h-5" />
            Add Application
          </Link>
        </div>

        {/* Statistics Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <StatCard
            title="Total Applications"
            value={stats?.total || 0}
            icon={<Briefcase className="w-6 h-6" />}
            color="bg-blue-500"
          />
          <StatCard
            title="In Progress"
            value={(stats?.applied || 0) + (stats?.screening || 0) + (stats?.interview || 0)}
            icon={<Clock className="w-6 h-6" />}
            color="bg-yellow-500"
          />
          <StatCard
            title="Offers"
            value={(stats?.offer || 0) + (stats?.accepted || 0)}
            icon={<CheckCircle className="w-6 h-6" />}
            color="bg-green-500"
          />
          <StatCard
            title="Rejected"
            value={stats?.rejected || 0}
            icon={<XCircle className="w-6 h-6" />}
            color="bg-red-500"
          />
        </div>

        {/* Status Breakdown */}
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-bold text-gray-900 mb-4 flex items-center gap-2">
            <TrendingUp className="w-5 h-5" />
            Application Status Breakdown
          </h2>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <StatusItem label="Applied" count={stats?.applied || 0} />
            <StatusItem label="Screening" count={stats?.screening || 0} />
            <StatusItem label="Interview" count={stats?.interview || 0} />
            <StatusItem label="Offer" count={stats?.offer || 0} />
          </div>
        </div>

        {/* Recent Applications */}
        <div className="bg-white rounded-lg shadow">
          <div className="p-6 border-b border-gray-200">
            <div className="flex justify-between items-center">
              <h2 className="text-xl font-bold text-gray-900">Recent Applications</h2>
              <Link
                to="/jobs"
                className="text-indigo-600 hover:text-indigo-700 text-sm font-medium"
              >
                View All
              </Link>
            </div>
          </div>
          <div className="divide-y divide-gray-200">
            {recentJobs.length === 0 ? (
              <div className="p-6 text-center text-gray-500">
                No applications yet. Start by adding your first job application!
              </div>
            ) : (
              recentJobs.map((job) => (
                <Link
                  key={job.id}
                  to={`/jobs/${job.id}`}
                  className="block p-6 hover:bg-gray-50 transition-colors"
                >
                  <div className="flex justify-between items-start">
                    <div className="flex-1">
                      <h3 className="text-lg font-semibold text-gray-900">{job.title}</h3>
                      <p className="text-gray-600 mt-1">{job.company}</p>
                      <p className="text-sm text-gray-500 mt-2">
                        Applied: {new Date(job.appliedDate).toLocaleDateString()}
                      </p>
                    </div>
                    <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(job.status)}`}>
                      {job.status}
                    </span>
                  </div>
                </Link>
              ))
            )}
          </div>
        </div>
      </div>
    </Layout>
  );
};

const StatCard = ({ title, value, icon, color }) => (
  <div className="bg-white rounded-lg shadow p-6">
    <div className="flex items-center justify-between">
      <div>
        <p className="text-sm text-gray-600">{title}</p>
        <p className="text-3xl font-bold text-gray-900 mt-2">{value}</p>
      </div>
      <div className={`${color} text-white p-3 rounded-lg`}>
        {icon}
      </div>
    </div>
  </div>
);

const StatusItem = ({ label, count }) => (
  <div className="text-center">
    <p className="text-2xl font-bold text-gray-900">{count}</p>
    <p className="text-sm text-gray-600 mt-1">{label}</p>
  </div>
);

export default Dashboard;