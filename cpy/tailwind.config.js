/** @type {import('tailwindcss').Config} */
import defaultTheme from 'tailwindcss/defaultTheme'

export default {
  darkMode: ['class'],
  content: [
    './pages/**/*.{ts,tsx}',
    './components/**/*.{ts,tsx}',
    './app/**/*.{ts,tsx}',
    './src/**/*.{ts,tsx}',
  ],
  theme: {
  	extend: {
  		borderRadius: {
  			lg: 'var(--radius)',
  			md: 'calc(var(--radius) - 2px)',
  			sm: 'calc(var(--radius) - 4px)'
  		},
  		colors: {
  			orange: {
  				ps: {
  					'100': '#FFDD99',
  					'200': '#FFB84D',
  					'300': '#FF9F1A',
  					'400': '#FF8500',
  					'500': '#FF5A00',
  					'600': '#E04D00',
  					'700': '#B93F00',
  					'800': '#9F3400',
  					'900': '#7A2400'
  				},
  				pc: {
  					'50': '#FFF4EC',
  					'100': '#FFE3D2',
  					'200': '#FFC9A8',
  					'300': '#FFAF7E',
  					'400': '#FF9554',
  					'500': '#FF5A00',
  					'600': '#E04F00',
  					'700': '#B93F00',
  					'800': '#922F00',
  					'900': '#6B1F00'
  				}
  			},
  			blue: {
  				ps: {
  					'50': '#E8F0FE',
  					'100': '#D2E3FC',
  					'200': '#AECBFA',
  					'300': '#8AB4F8',
  					'400': '#669DF6',
  					'500': '#4285F4',
  					'600': '#1A73E8',
  					'700': '#1967D2',
  					'800': '#185ABC',
  					'850': '#174EA6',
  					'900': '#0D47A1',
  					'950': '#0B3C91'
  				},
  				pa: {
  					'50': '#FCFDFE',
  					'100': '#F6F9FF',
  					'200': '#EBF2FF',
  					'300': '#DCEAFF',
  					'400': '#CBE0FF',
  					'500': '#B7D3FF',
  					'600': '#9FC0FF',
  					'700': '#7DA7F8',
  					'800': '#2563EB',
  					'850': '#255DD7',
  					'900': '#2057D1',
  					'950': '#152E63'
  				}
  			},
  			background: 'hsl(var(--background))',
  			foreground: 'hsl(var(--foreground))',
  			card: {
  				DEFAULT: 'hsl(var(--card))',
  				foreground: 'hsl(var(--card-foreground))'
  			},
  			popover: {
  				DEFAULT: 'hsl(var(--popover))',
  				foreground: 'hsl(var(--popover-foreground))'
  			},
  			primary: {
  				DEFAULT: 'hsl(var(--primary))',
  				foreground: 'hsl(var(--primary-foreground))'
  			},
  			secondary: {
  				DEFAULT: 'hsl(var(--secondary))',
  				foreground: 'hsl(var(--secondary-foreground))'
  			},
  			muted: {
  				DEFAULT: 'hsl(var(--muted))',
  				foreground: 'hsl(var(--muted-foreground))'
  			},
  			accent: {
  				DEFAULT: 'hsl(var(--accent))',
  				foreground: 'hsl(var(--accent-foreground))'
  			},
  			destructive: {
  				DEFAULT: 'hsl(var(--destructive))',
  				foreground: 'hsl(var(--destructive-foreground))'
  			},
  			border: 'hsl(var(--border))',
  			input: 'hsl(var(--input))',
  			ring: 'hsl(var(--ring))',
  			chart: {
  				'1': 'hsl(var(--chart-1))',
  				'2': 'hsl(var(--chart-2))',
  				'3': 'hsl(var(--chart-3))',
  				'4': 'hsl(var(--chart-4))',
  				'5': 'hsl(var(--chart-5))'
  			},
  			sidebar: {
  				DEFAULT: 'hsl(var(--sidebar-background))',
  				foreground: 'hsl(var(--sidebar-foreground))',
  				primary: 'hsl(var(--sidebar-primary))',
  				'primary-foreground': 'hsl(var(--sidebar-primary-foreground))',
  				accent: 'hsl(var(--sidebar-accent))',
  				'accent-foreground': 'hsl(var(--sidebar-accent-foreground))',
  				border: 'hsl(var(--sidebar-border))',
  				ring: 'hsl(var(--sidebar-ring))'
  			}
  		},
  		keyframes: {
  			'accordion-down': {
  				from: {
  					height: '0'
  				},
  				to: {
  					height: 'var(--radix-accordion-content-height)'
  				}
  			},
  			'accordion-up': {
  				from: {
  					height: 'var(--radix-accordion-content-height)'
  				},
  				to: {
  					height: '0'
  				}
  			}
  		},
  		animation: {
  			'accordion-down': 'accordion-down 0.2s ease-out',
  			'accordion-up': 'accordion-up 0.2s ease-out'
  		},
  		fontFamily: {
  			inter: [
  				'Inter',
  				'Inter Regular',
  				'Inter Bold',
  				'Inter ExtraBold',
  				'Inter Light',
  				'Inter Medium',
  				'Inter SemiBold',
  				'Inter Thin',
  				'Inter Black',
                    ...defaultTheme.fontFamily.sans
                ],
  			sans: [
  				'Geist Sans',
  				'Geist Sans Regular',
  				'Geist Sans Bold',
  				'Geist Sans Light',
                    ...defaultTheme.fontFamily.sans
                ]
  		}
  	}
  },
  plugins: [require('tailwindcss-animate')],
};
