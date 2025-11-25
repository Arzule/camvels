/* Dashboard - Carga y renderiza gráficos estadísticos */
(function(contextPath) {
    'use strict';

    async function cargarEstadisticas() {
        try {
            const response = await fetch(contextPath + '/DashboardServlet?accion=stats');
            if (!response.ok) {
                throw new Error('Error al cargar estadísticas: ' + response.status);
            }
            const data = await response.json();
            
            // Verificar que Chart.js esté disponible
            if (typeof Chart === 'undefined') {
                console.error('Chart.js no está cargado');
                return;
            }
            
            renderizarProductosPorCategoria(data.productosPorCategoria || {});
            renderizarStockPorCategoria(data.stockPorCategoria || {});
            renderizarMovimientosPorTipo(data.movimientosPorTipo || {});
            renderizarStockPorEstado(data.estadisticasStock || {});
            renderizarMovimientosPorFecha(data.movimientosPorFecha || {});
            renderizarTopProductos(data.topProductos || []);
            
        } catch (error) {
            console.error('Error cargando estadísticas:', error);
        }
    }

    /* Gráfico de Productos por Categoría */
    function renderizarProductosPorCategoria(productosCategoria) {
        const canvas = document.getElementById('chartProductosCategoria');
        if (!canvas) return;
        
        const ctx = canvas.getContext('2d');
        new Chart(ctx, {
            type: 'pie',
            data: {
                labels: Object.keys(productosCategoria),
                datasets: [{
                    label: 'Productos',
                    data: Object.values(productosCategoria),
                    backgroundColor: [
                        '#6366f1', '#8b5cf6', '#ec4899', '#f43f5e', '#ef4444',
                        '#f97316', '#f59e0b', '#10b981', '#14b8a6', '#06b6d4'
                    ],
                    borderWidth: 2,
                    borderColor: '#ffffff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            padding: 15,
                            font: {
                                size: 12,
                                weight: '500'
                            }
                        }
                    }
                }
            }
        });
    }

    /* Gráfico de Stock por Categoría */
    function renderizarStockPorCategoria(stockCategoria) {
        const canvas = document.getElementById('chartStockCategoria');
        if (!canvas) return;
        
        const ctx = canvas.getContext('2d');
        const gradient = ctx.createLinearGradient(0, 0, 0, 400);
        gradient.addColorStop(0, 'rgba(99, 102, 241, 0.8)');
        gradient.addColorStop(1, 'rgba(99, 102, 241, 0.4)');
        
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: Object.keys(stockCategoria),
                datasets: [{
                    label: 'Stock Total',
                    data: Object.values(stockCategoria),
                    backgroundColor: gradient,
                    borderColor: '#6366f1',
                    borderWidth: 2,
                    borderRadius: 6,
                    borderSkipped: false
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        },
                        ticks: {
                            font: {
                                size: 11
                            }
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        },
                        ticks: {
                            font: {
                                size: 11
                            }
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: false
                    }
                }
            }
        });
    }

    /* Gráfico de Movimientos por Tipo */
    function renderizarMovimientosPorTipo(movimientosTipo) {
        const canvas = document.getElementById('chartMovimientosTipo');
        if (!canvas) return;
        
        const ctx = canvas.getContext('2d');
        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: Object.keys(movimientosTipo),
                datasets: [{
                    label: 'Cantidad',
                    data: Object.values(movimientosTipo),
                    backgroundColor: [
                        '#ef4444',
                        '#10b981',
                        '#f59e0b'
                    ],
                    borderWidth: 3,
                    borderColor: '#ffffff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            padding: 15,
                            font: {
                                size: 12,
                                weight: '500'
                            }
                        }
                    }
                }
            }
        });
    }

    /* Gráfico de Stock: Bueno vs Mal Estado */
    function renderizarStockPorEstado(estadisticasStock) {
        const canvas = document.getElementById('chartStockEstado');
        if (!canvas) return;
        
        const ctx = canvas.getContext('2d');
        new Chart(ctx, {
            type: 'pie',
            data: {
                labels: ['Buen Estado', 'Mal Estado'],
                datasets: [{
                    label: 'Stock',
                    data: [
                        estadisticasStock.buen_estado || 0,
                        estadisticasStock.mal_estado || 0
                    ],
                    backgroundColor: ['#10b981', '#ef4444'],
                    borderWidth: 3,
                    borderColor: '#ffffff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            padding: 15,
                            font: {
                                size: 12,
                                weight: '500'
                            }
                        }
                    }
                }
            }
        });
    }

    /* Gráfico de Movimientos por Fecha */
    function renderizarMovimientosPorFecha(movimientosFecha) {
        const canvas = document.getElementById('chartMovimientosFecha');
        if (!canvas) return;
        
        const ctx = canvas.getContext('2d');
        const fechas = Object.keys(movimientosFecha).sort();
        const tiposMovimiento = ['ENTRADA', 'SALIDA', 'AJUSTE'];
        
        const datasetsPorTipo = tiposMovimiento.map(tipo => {
            const colores = {
                'ENTRADA': { bg: 'rgba(16, 185, 129, 0.15)', border: '#10b981', pointBg: '#10b981' },
                'SALIDA': { bg: 'rgba(239, 68, 68, 0.15)', border: '#ef4444', pointBg: '#ef4444' },
                'AJUSTE': { bg: 'rgba(245, 158, 11, 0.15)', border: '#f59e0b', pointBg: '#f59e0b' }
            };
            const color = colores[tipo] || colores['ENTRADA'];
            return {
                label: tipo,
                data: fechas.map(fecha => movimientosFecha[fecha] && movimientosFecha[fecha][tipo] ? movimientosFecha[fecha][tipo] : 0),
                backgroundColor: color.bg,
                borderColor: color.border,
                pointBackgroundColor: color.pointBg,
                pointBorderColor: '#ffffff',
                pointBorderWidth: 2,
                pointRadius: 5,
                pointHoverRadius: 7,
                borderWidth: 3,
                fill: true,
                tension: 0.4
            };
        });

        new Chart(ctx, {
            type: 'line',
            data: {
                labels: fechas.map(f => {
                    const date = new Date(f);
                    return date.toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit' });
                }),
                datasets: datasetsPorTipo
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        },
                        ticks: {
                            font: {
                                size: 11
                            }
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        },
                        ticks: {
                            font: {
                                size: 11
                            }
                        }
                    }
                },
                plugins: {
                    legend: {
                        position: 'top',
                        labels: {
                            padding: 20,
                            font: {
                                size: 12,
                                weight: '500'
                            },
                            usePointStyle: true,
                            pointStyle: 'circle'
                        }
                    }
                },
                interaction: {
                    intersect: false,
                    mode: 'index'
                }
            }
        });
    }

    /* Gráfico de Top 10 Productos por Stock */
    function renderizarTopProductos(topProductos) {
        const canvas = document.getElementById('chartTopProductos');
        if (!canvas || topProductos.length === 0) return;
        
        const ctx = canvas.getContext('2d');
        const labels = topProductos.map(p => {
            const nombre = p[1] || 'Sin nombre';
            return nombre.length > 20 ? nombre.substring(0, 20) + '...' : nombre;
        });
        const stocks = topProductos.map(p => p[2] || 0);

        // Crear gradiente para cada barra
        const gradient = ctx.createLinearGradient(0, 0, 600, 0);
        gradient.addColorStop(0, 'rgba(139, 92, 246, 0.8)');
        gradient.addColorStop(1, 'rgba(99, 102, 241, 0.8)');

        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Stock',
                    data: stocks,
                    backgroundColor: gradient,
                    borderColor: '#8b5cf6',
                    borderWidth: 2,
                    borderRadius: 4,
                    borderSkipped: false
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                indexAxis: 'y',
                scales: {
                    x: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        },
                        ticks: {
                            font: {
                                size: 11
                            }
                        }
                    },
                    y: {
                        grid: {
                            display: false
                        },
                        ticks: {
                            font: {
                                size: 11
                            }
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: false
                    }
                }
            }
        });
    }

    // Inicializar cuando el DOM esté listo
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', cargarEstadisticas);
    } else {
        cargarEstadisticas();
    }

})(window.CONTEXT_PATH || '');

