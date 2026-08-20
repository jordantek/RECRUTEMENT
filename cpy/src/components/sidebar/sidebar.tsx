import {
  LayoutDashboard,
  Users,
  Settings,
  LogOut,
  Dot,
  ShieldCheckIcon,
  ChevronDown,
  Menu,
  UserCog,
  FileText,
  Building2,
  FileSearch,
  BookOpen,
  Wallet,
  Calculator,
  BarChart3,
  BookText,
  Gavel,
} from 'lucide-react'
import { useLocation, Link, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { motion } from 'framer-motion'
import { genralConfig } from '../../../config'
import { routeHelpers } from '@/helpers/routeHelpers'
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
  TooltipProvider,
} from '@/components/ui/tooltip'

const sidebarStyles = {
  sidebar: "bg-white h-screen border-r border-gray-200 shadow-sm flex flex-col transition-all w-fit",
  header: "flex items-center justify-between px-4 py-3 border-b border-gray-200 bg-white/90 backdrop-blur-sm",
  appName: "text-xl font-semibold whitespace-nowrap text-blue-600 bg-blue-100/80 px-2 py-1 rounded-lg",
  sectionTitle: "px-3 text-xs font-semibold uppercase text-blue-800 tracking-wider mt-4 mb-1",
  navItem: {
    base: "w-full flex items-center gap-2 px-3 py-2.5 rounded-md transition text-sm font-medium",
    active: "bg-blue-100 text-blue-700 shadow-sm",
    inactive: "bg-transparent hover:bg-blue-50 text-gray-600 hover:text-blue-600",
    icon: "flex-shrink-0"
  },
  subMenu: {
    container: "ml-6 mt-1 space-y-1 border-l-2 pl-3 border-blue-200",
    item: {
      base: "flex items-center py-1.5 text-xs rounded-md transition",
      active: "text-blue-700 font-medium",
      inactive: "text-gray-500 hover:text-blue-600"
    }
  },
  footer: "p-3 border-t border-gray-200 bg-white/80 backdrop-blur-sm"
}

export function Sidebar({ onLogout }: { onLogout: () => void }) {
  const { pathname } = useLocation()
  const navigate = useNavigate()
  const [collapsed, setCollapsed] = useState(false)
  const [openMenus, setOpenMenus] = useState<{ [key: string]: boolean }>({})

  const toggleMenu = (name: string) => {
    setOpenMenus((prev) => ({ ...prev, [name]: !prev[name] }))
  }

  const toggleCollapse = () => {
    if (!collapsed) {
      setOpenMenus({})
    }
    setCollapsed(!collapsed)
  }

  const isSubRouteActive = (subMenu?: { href: string }[]) => {
    return subMenu?.some((s) => pathname.startsWith(s.href))
  }

  const employeeNavigation = [
    {
      name: 'Accueil',
      href: routeHelpers.dashboard.employee.home,
      icon: LayoutDashboard,
    },
    {
      name: 'Mes demandes',
      href: routeHelpers.dashboard.employee.absenceRequests,
      icon: FileText,
    },
    {
      name: 'Mes Bulletins de paie',
      href: routeHelpers.dashboard.employee.paySlips,
      icon: FileSearch,
    },
    {
      name: 'Contrats',
      href: routeHelpers.dashboard.employee.contracts,
      icon: Calculator,
    },
    {
      name: 'Formations',
      href: routeHelpers.dashboard.employee.formation,
      icon: BookOpen,
    },
    {
      name: 'Historiques',
      href: routeHelpers.dashboard.employee.history,
      icon: BarChart3,
    },
    {
      name: 'Sanctions',
      href: routeHelpers.dashboard.employee.sanctions,
      icon: Gavel,
    },
    {
      name: 'Modèles de documents',
      href: routeHelpers.dashboard.docTemplate.index,
      icon: BookText,
    },
    {
      name: 'Paramètres système',
      href: routeHelpers.dashboard.settings.index,
      icon: Settings,
    },
  ]

  const adminNavigation = [
    {
      name: 'Accueil',
      href: routeHelpers.dashboard.index,
      icon: LayoutDashboard,
    },
    {
      name: 'Entreprises',
      href: '#',
      icon: Building2,
      subMenu: [
        { name: 'Gestion des entreprises', href: routeHelpers.dashboard.company.index },
        { name: "Création d'une entreprise", href: routeHelpers.dashboard.company.create },
      ],
    },
    {
      name: 'Employés',
      href: '#',
      icon: Users,
      subMenu: [
        { name: 'Gestion des employés', href: routeHelpers.dashboard.employee.index },
        { name: "Création d'un employé", href: routeHelpers.dashboard.employee.create },
      ],
    },
    {
      name: 'Gestion administrative',
      href: routeHelpers.dashboard.administrativeManagement.index,
      icon: ShieldCheckIcon,
    },
    {
      name: 'Traitement salaire',
      href: '#',
      icon: Wallet,
      subMenu: [
        { name: 'Accessoire salaire', href: routeHelpers.dashboard.payrollManagement.salaryAccessory },
        { name: 'Eléments de rémunération', href: routeHelpers.dashboard.payrollManagement.salaryProcessing },
        { name: 'Calcul et sauvegarde', href: routeHelpers.dashboard.payrollManagement.backup },
        { name: 'Solde tout compte', href: routeHelpers.dashboard.payrollManagement.solde },
        { name: 'Edition des états', href: routeHelpers.dashboard.payrollManagement.paySheet },
        { name: 'Résapitulatif', href: routeHelpers.dashboard.payrollManagement.summary },
        { name: 'Etat des virements', href: routeHelpers.dashboard.payrollManagement.stateOfTransfers },
        { name: 'Edition des bulletins de paie', href: routeHelpers.dashboard.payrollManagement.paySlips },
      ],
    },
    {
      name: 'Simulation salaire',
      href: '#',
      icon: Calculator,
      subMenu: [
        { name: 'Brut au net', href: routeHelpers.dashboard.simulation.brutToNet },
        { name: 'Net au brut', href: routeHelpers.dashboard.simulation.netToBrut },
      ],
    },
    {
      name: 'Journal RH',
      href: routeHelpers.dashboard.journalRH.index,
      icon: BookText,
    },
    {
      name: 'Tableau de bord RH',
      href: routeHelpers.dashboard.employee.rh,
      icon: BarChart3,
    },
    {
      name: 'Gestion du personnel',
      href: routeHelpers.dashboard.staff.index,
      icon: UserCog,
    },
    {
      name: "Modèles de documents",
      href: routeHelpers.dashboard.docTemplate.index,
      icon: FileText,
    },
    {
      name: 'Paramètres système',
      href: routeHelpers.dashboard.settings.index,
      icon: Settings,
    },
  ]

  const renderNavItem = (item: any) => {
    const isMenuActive = pathname === item.href || isSubRouteActive(item.subMenu)
    const Icon = item.icon

    if (!item.subMenu) {
      return (
        <Tooltip key={item.name} delayDuration={100}>
          <TooltipTrigger asChild>
            <Link to={item.href} className="mb-1 block">
              <div
                className={`${sidebarStyles.navItem.base} ${
                  isMenuActive ? sidebarStyles.navItem.active : sidebarStyles.navItem.inactive
                }`}
              >
                <Icon size={18} className={sidebarStyles.navItem.icon} />
                {!collapsed && <span className="truncate">{item.name}</span>}
              </div>
            </Link>
          </TooltipTrigger>
          {collapsed && (
            <TooltipContent side="right">
              {item.name}
            </TooltipContent>
          )}
        </Tooltip>
      )
    }

    return (
      <div key={item.name} className="mb-1">
        <Tooltip delayDuration={100}>
          <TooltipTrigger asChild>
            <button
              className={`${sidebarStyles.navItem.base} ${
                isMenuActive ? sidebarStyles.navItem.active : sidebarStyles.navItem.inactive
              }`}
              onClick={() => {
                if (collapsed && item.subMenu?.length > 0) {
                  navigate(item.subMenu[0].href)
                  return
                }
                toggleMenu(item.name)
              }}
            >
              <Icon size={18} className={sidebarStyles.navItem.icon} />
              {!collapsed && <span className="truncate">{item.name}</span>}
              {!collapsed && item.subMenu && (
                <ChevronDown
                  size={14}
                  className={`ml-auto transition-transform ${openMenus[item.name] ? 'rotate-180' : ''}`}
                />
              )}
            </button>
          </TooltipTrigger>
          {collapsed && (
            <TooltipContent side="right">
              {item.name}
            </TooltipContent>
          )}
        </Tooltip>

        {item.subMenu && openMenus[item.name] && !collapsed && (
          <div className={sidebarStyles.subMenu.container}>
            {item.subMenu.map((subItem: any) => {
              const isActive = pathname === subItem.href
              return (
                <Link
                  key={subItem.href}
                  to={subItem.href}
                  className={`${sidebarStyles.subMenu.item.base} ${
                    isActive ? sidebarStyles.subMenu.item.active : sidebarStyles.subMenu.item.inactive
                  }`}
                >
                  <Dot size={18} className="mr-1" />
                  <span className="truncate">{subItem.name}</span>
                </Link>
              )
            })}
          </div>
        )}
      </div>
    )
  }

  return (
    <TooltipProvider>
      <motion.aside
        animate={{ width: collapsed ? 80 : 240 }}
        transition={{ duration: 0.2, ease: "easeInOut" }}
        className={sidebarStyles.sidebar}
      >
        {/* Header */}
        <div className={sidebarStyles.header}>
          {!collapsed ? (
            <Link to={routeHelpers.dashboard.index} className="flex items-center">
              <span className={sidebarStyles.appName}>
                {genralConfig.appName}
              </span>
            </Link>
          ) : (
            <div className="w-6 h-6" />
          )}
          <button
            onClick={toggleCollapse}
            className="p-1 rounded-md hover:bg-gray-100 text-gray-500 hover:text-gray-700 transition"
            aria-label={collapsed ? "Expand sidebar" : "Collapse sidebar"}
          >
            <Menu size={20} />
          </button>
        </div>

        {/* Navigation */}
        <nav className="flex-1 overflow-y-auto py-4 px-2 scroll-hidden space-y-1">
          {!collapsed && (
            <div className={sidebarStyles.sectionTitle}>Administrateur</div>
          )}
          {adminNavigation.map((item) => renderNavItem(item))}

          {!collapsed && (
            <div className={sidebarStyles.sectionTitle}>Employés</div>
          )}
          {employeeNavigation.map((item) => renderNavItem(item))}
        </nav>

        {/* Footer */}
        <div className={sidebarStyles.footer}>
          <button
            onClick={onLogout}
            className="w-full flex items-center gap-2 text-red-500 hover:bg-red-50 px-3 py-2 rounded-md transition"
          >
            <LogOut size={18} />
            {!collapsed && <span className="font-medium">Déconnexion</span>}
          </button>
        </div>
      </motion.aside>
    </TooltipProvider>
  )
}
