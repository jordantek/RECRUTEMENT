import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { RootLayout } from '@/components/layout/root-layout';
import { LoginPage } from '@/pages/auth/login';
import { AuthLayout } from '@/components/layout/auth-layout';
import { DashboardLayout } from '@/components/layout/dashboard-layout';
import { DashboardPage } from '@/pages/app/dasboard';

import  EmployeeHomePage  from '@/pages/app/employee-home/EmployeeHomePage';
import { EmployeeAbsenceRequestPage } from '@/pages/app/employee-home/employee-absence-request/EmployeeAbsenceRequestPage';

import { StaffManagePage } from '@/pages/app/staff-management/StaffManagePage';
import { AdministrativeManagePage } from '@/pages/app/administrative-management/AdministrativeManagePage';
import { CompanyPage } from '@/pages/app/company/CompanyPage';
import {routeHelpers} from "@/helpers/routeHelpers.ts";
import {CreateCompanyPage} from "@/pages/app/company/create-edit/CreateCompanyPage.tsx";
import {CreateEmployeePage} from "@/pages/app/employee/CreateEmployeePage.tsx";
import {DashboardRhPage} from "@/pages/app/employee/DashboardRhPage.tsx";

import { EmployeePage } from '@/pages/app/employee/EmployeePage';

import { NetToBrutPage } from '@/pages/app/simulation/NetToBrutPage';
import { BrutToNetPage } from '@/pages/app/simulation/BrutToNetPage';

import { SalaryProcessingPage } from '@/pages/app/payroll-management/salary-processing/SalaryProcessingPage';
import { SalaryAccessoryPage } from '@/pages/app/payroll-management/salary-accessory/SalaryAccessoryPage';
import { StateOfTransfersPage } from '@/pages/app/payroll-management/state-of-transfers/StateOfTransfersPage';
import { PaySlipsPage } from '@/pages/app/payroll-management/pay-slips/PaySlipsPage';
import { BackupPage } from '@/pages/app/payroll-management/backup/BackupPage';
import { SummaryPage } from '@/pages/app/payroll-management/summary/SummaryPage';

import {CreateEmployeeContratPage} from "@/pages/app/company/contrat/CreateEmployeeContratPage.tsx";
import {SettingGeneralPage} from "@/pages/app/setting-general/setting-general-page.tsx";
import {SettingInfoGeneralPage} from "@/pages/app/setting-general/info-general/setting-info-general-page.tsx";
import {SettingPayrollPage} from "@/pages/app/setting-general/payroll/setting-payroll-page.tsx";
import { StateSettingsPage } from './pages/app/state-settings/StateSettingsPage';
import {DocTemplatePage} from "@/pages/app/doc-template/DocTemplatePage.tsx";
import { PaySheetPage } from './pages/app/payroll-management/pay-sheet/PaySheetPage';
import {JournalRHPage} from "@/pages/app/journal-rh/JournalRHPage.tsx";
import EmployeeFormationPage from './pages/app/employee-home/employee-formation/EmployeeFormationPage';
import EmployeeSanctionsPage from './pages/app/employee-home/employee-sanctions/EmployeeSanctionsPage';
import { FinalSettlementPage } from './pages/app/payroll-management/final-settlement/FinalSettlementPage';

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<RootLayout />}>
                    <Route path="/state/" element={<DashboardLayout />}>
                        <Route path="setting" element={<StateSettingsPage />} />
                    </Route>
                    
                    {/* Auth routes */}
                    <Route path="/" element={<AuthLayout />}>
                        <Route path="login" element={<LoginPage />} />
                    </Route>

                    {/* App routes */}
                    <Route path="dashboard" element={<DashboardLayout />}>
                        <Route index element={<DashboardPage />} />
                        <Route path="staff/management" element={<StaffManagePage />} />

                        {/* Routes des entreprises */}
                        <Route path="company" element={<CompanyPage />} />
                        <Route path={routeHelpers.dashboard.company.create} element={<CreateCompanyPage />} />
                        <Route path={routeHelpers.dashboard.company.contract.createPath} element={<CreateEmployeeContratPage />} />

                        {/* Routes des employés */}
                        <Route path={routeHelpers.dashboard.employee.index} element={<EmployeePage />} />
                        <Route path={routeHelpers.dashboard.employee.create} element={<CreateEmployeePage/>} />

                        <Route path={routeHelpers.dashboard.employee.home} element={<EmployeeHomePage/>} />
                        <Route path={routeHelpers.dashboard.employee.absenceRequests} element={<EmployeeAbsenceRequestPage/>} />
                        <Route path={routeHelpers.dashboard.employee.formation} element={<EmployeeFormationPage/>} />
                        <Route path={routeHelpers.dashboard.employee.sanctions} element={<EmployeeSanctionsPage/>} />

                        {/* Routes gestion adm */}
                        <Route path={routeHelpers.dashboard.administrativeManagement.index} element={<AdministrativeManagePage />} />

                        {/* Routes de simulations */}
                        <Route path={routeHelpers.dashboard.simulation.brutToNet} element={<BrutToNetPage />} />
                        <Route path={routeHelpers.dashboard.simulation.netToBrut} element={<NetToBrutPage />} />

                        {/* Routes de gestion de paie */}
                        <Route path={routeHelpers.dashboard.payrollManagement.salaryAccessory} element={<SalaryAccessoryPage />} />
                        <Route path={routeHelpers.dashboard.payrollManagement.salaryProcessing} element={<SalaryProcessingPage />} />
                        <Route path={routeHelpers.dashboard.payrollManagement.stateOfTransfers} element={<StateOfTransfersPage />} />
                        <Route path={routeHelpers.dashboard.payrollManagement.paySlips} element={<PaySlipsPage />} />
                        <Route path={routeHelpers.dashboard.payrollManagement.backup} element={<BackupPage />} />
                        <Route path={routeHelpers.dashboard.payrollManagement.paySheet} element={<PaySheetPage />} />
                        <Route path={routeHelpers.dashboard.payrollManagement.summary} element={<SummaryPage />} />
                        <Route path={routeHelpers.dashboard.payrollManagement.solde} element={<FinalSettlementPage />} />

                        {/*Route de templat de doc administraive*/}
                        <Route path={routeHelpers.dashboard.docTemplate.index} element={<DocTemplatePage />}/>

                        {/* Routes tests */}
                        <Route path={routeHelpers.dashboard.employee.rh} element={<DashboardRhPage/>} />

                        {/*Routes de journal RH*/}
                        <Route path={routeHelpers.dashboard.journalRH.index} element={<JournalRHPage/>} />

                        {/* Routes des parametres */}
                        <Route path={routeHelpers.dashboard.settings.index} element={<SettingGeneralPage />} />
                        <Route path={routeHelpers.dashboard.settings.general} element={<SettingInfoGeneralPage />} />
                        <Route path={routeHelpers.dashboard.settings.payroll} element={<SettingPayrollPage />} />
                    </Route>

                    {/* Redirect unknown routes */}
                    <Route path="*" element={<Navigate to="/login" replace />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
