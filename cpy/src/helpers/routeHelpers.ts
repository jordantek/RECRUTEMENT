
export const routeHelpers = {
    auth: {
        login: "/login",
       /* forgetPassword: "/forgot-password",
        pasword: {
            reset: "/password/reset",
        }*/
    },
    dashboard: {
        index: "/dashboard",
        staff:{
          index:"/dashboard/staff/management",
        },
        company:{
            index:"/dashboard/company",
            create:"/dashboard/company/create",
            contract: {
                createPath: "/dashboard/company/:company_id/:company_name/contract/create",
                create: (company_id: number | string, company_name: string) =>
                    `/dashboard/company/${company_id}/${encodeURIComponent(company_name)}/contract/create`,
            },

        },
        employee:{
            index:"/dashboard/employee",
            home: "/dashboard/employee/home",
            create:"/dashboard/employee/create",
            manage:"/dashboard/employee/manage",
            rh:"/dashboard/rh",
            absenceRequests: "/dashboard/employee/absence-requests",
            contracts: "/dashboard/employee/contracts",
            paySlips: "/dashboard/employee/payslips",
            formation: "/dashboard/employee/formation",
            history: "/dashboard/employee/history",
            sanctions: "/dashboard/employee/sanctions",
            documents: "/dashboard/employee/documents",
            

        },
        administrativeManagement : {
            index: "/dashboard/administrative-management",
        },

        simulation : {
            netToBrut: "/dashboard/simulation/net-to-brut",
            brutToNet: "/dashboard/simulation/brut-to-net",
        },
        
        payrollManagement : {
            salaryAccessory: "/dashboard/payroll/salary-accessory",
            backup: "/dashboard/payroll/balance-sheet",
            paySlips: "/dashboard/payroll/pay-slips",
            salaryProcessing: "/dashboard/payroll/salary-processing",
            stateOfTransfers: "/dashboard/payroll/state-of-transfers",
            summary: "/dashboard/payroll/summary",
            paySheet: "/dashboard/payroll/pay-sheet",
            solde: "/dashboard/payroll/solde-compte",
        },

        settings: {
            index: "/dashboard/settings",
            general: "/dashboard/settings/general",
            payroll: "/dashboard/settings/payroll",
            paymentMethods: "/dashboard/settings/payment-methods",
            infoGeneral: "/dashboard/settings/info-general",
            notifications: "/dashboard/settings/notifications",
        },

        docTemplate:{
          index:"/dashboard/doc-template",
        },

        journalRH:{
            index:"/dashboard/journal-rh",
        },

        notifications: "/dashboard/notifications",
    },
    
    composeWithQueries: (url: string, queries: { [key: string]: string|number }) => {
        const searchParams = new URLSearchParams();
        Object.entries(queries).forEach(([key, value]) => {
            searchParams.append(key, value as string);
        });
        return `${url}?${searchParams.toString()}`
    },

    composeWithParams: (url: string, params: { [key: string]: string|number }) => {
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        return url.replace(/:([a-zA-Z0-9_]+)/g, (match, key) => {
            if (params[key] !== undefined) {
                return params[key].toString(); // Replace the placeholder
            }
            throw new Error(`Missing parameter for :${key}`);
        });
    },
};