import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from scipy.stats import chi2_contingency, fisher_exact
import os
import math
from matplotlib.patches import Patch

# Set style for better visualizations
plt.style.use('ggplot')
sns.set(font_scale=1.1)
sns.set_style("whitegrid")

# Define custom color palette
custom_palette = {
    'SO': '#1f77b4',  # Blue
    'GPT': '#ff7f0e',  # Orange
    'Both': '#2ca02c',  # Green
    'Neither': '#d62728'  # Red
}

# Define path to dataset
path = "final/ML-code-smell-detection-main/finall_dataset_so_vs_gpt .csv"
df = pd.read_csv(path)

print("\nDataset Information:")
print(f"Number of rows: {len(df)}")
print(f"Number of columns: {len(df.columns)}")
print("\nColumn names:")
print(df.columns.tolist())

# Process cyclomatic complexity
print("\nProcessing cyclomatic complexity")

so_cc_col = [col for col in df.columns if 'Cyclomatic complexity(so)' in col]
gpt_cc_col = [col for col in df.columns if 'Cyclomatic complexity(gpt)' in col]

if so_cc_col and gpt_cc_col:
    so_cc_col = so_cc_col[0]
    gpt_cc_col = gpt_cc_col[0]
    
    df['CC_threshold_so'] = df[so_cc_col].apply(lambda x: 'TRUE' if x > 3 else 'FALSE')
    df['CC_threshold_gpt'] = df[gpt_cc_col].apply(lambda x: 'TRUE' if x > 3 else 'FALSE')
    
    print("\nStackOverflow Cyclomatic Complexity > 3:")
    print(df['CC_threshold_so'].value_counts())
    
    print("\nGPT Cyclomatic Complexity > 3:")
    print(df['CC_threshold_gpt'].value_counts())
else:
    print("Cyclomatic complexity columns not found in the dataset")

# Get metrics columns
so_metrics = [col for col in df.columns if '(so)' in col and 'code_snippet' not in col]
gpt_metrics = [col for col in df.columns if '(gpt)' in col and 'code_snippet' not in col]

print(f"Found {len(so_metrics)} StackOverflow metrics and {len(gpt_metrics)} GPT metrics")

# Function to calculate association metrics from a contingency table
def calculate_association_metrics(table):
    # Extract values from the table
    try:
        # Make sure we're handling string indices
        a = table.loc['True', 'True']   # True, True
        b = table.loc['True', 'False']  # True, False
        c = table.loc['False', 'True']  # False, True
        d = table.loc['False', 'False'] # False, False
        
        # Calculate Phi coefficient
        n = a + b + c + d
        
        # Avoid division by zero for phi coefficient
        denominator = (a+b) * (c+d) * (a+c) * (b+d)
        phi = (a*d - b*c) / math.sqrt(denominator) if denominator > 0 else 0
        
        # Calculate Odds ratio with handling for zero cells
        if b*c == 0:
            # Add a small constant to zero cells for odds ratio calculation
            adj_b = b if b > 0 else 0.5
            adj_c = c if c > 0 else 0.5
            odds_ratio = (a*d) / (adj_b*adj_c)
        else:
            odds_ratio = (a*d) / (b*c)
        
        # Calculate conditional probabilities
        p_gpt_given_so = a / (a + c) if (a + c) > 0 else 0  # P(GPT has debt | SO has debt)
        p_gpt_given_not_so = b / (b + d) if (b + d) > 0 else 0  # P(GPT has debt | SO has no debt)
        p_so_given_gpt = a / (a + b) if (a + b) > 0 else 0  # P(SO has debt | GPT has debt)
        p_so_given_not_gpt = c / (c + d) if (c + d) > 0 else 0  # P(SO has debt | GPT has no debt)
        
        # Calculate distribution percentages
        both_pct = (a / n) * 100  # Both have TD
        only_gpt_pct = (b / n) * 100  # Only GPT has TD
        only_so_pct = (c / n) * 100  # Only SO has TD
        neither_pct = (d / n) * 100  # Neither has TD
        
        # Calculate prevalence rates
        gpt_rate = ((a + b) / n) * 100  # GPT TD rate
        so_rate = ((a + c) / n) * 100  # SO TD rate
        difference = so_rate - gpt_rate  # SO - GPT difference
        
        return {
            'phi': phi,
            'odds_ratio': odds_ratio,
            'p_gpt_given_so': p_gpt_given_so,
            'p_gpt_given_not_so': p_gpt_given_not_so,
            'p_so_given_gpt': p_so_given_gpt,
            'p_so_given_not_gpt': p_so_given_not_gpt,
            'both_pct': both_pct,
            'only_gpt_pct': only_gpt_pct,
            'only_so_pct': only_so_pct,
            'neither_pct': neither_pct,
            'gpt_rate': gpt_rate,
            'so_rate': so_rate,
            'difference': difference,
            'counts': {
                'both': a,
                'only_gpt': b,
                'only_so': c,
                'neither': d,
                'total': n
            }
        }
    except Exception as e:
        print(f"Error calculating association metrics: {e}")
        return None

# Perform statistical tests between corresponding SO and GPT metrics
print("\n--- Statistical Tests and Association Metrics: SO vs GPT for each metric ---")

all_results = []
distribution_data = []

for so_metric, gpt_metric in zip(so_metrics, gpt_metrics):
    # Skip if not categorical
    if df[so_metric].dtype == 'float64' or df[gpt_metric].dtype == 'float64':
        continue
    
    # Create contingency table (False/True for consistency)
    # Convert values to boolean for consistent table formatting
    df_temp = df.copy()
    # Determine if columns are boolean or have other values (like True/False strings)
    if df[so_metric].dtype == bool:
        pass  # Already boolean
    elif set(df[so_metric].dropna().unique()).issubset({True, False, 'True', 'False', 'TRUE', 'FALSE'}):
        df_temp[so_metric] = df_temp[so_metric].map(lambda x: x == True or x == 'True' or x == 'TRUE')
    else:
        # For non-boolean categorical, convert to boolean based on presence of any value
        df_temp[so_metric] = df_temp[so_metric].notna() & (df_temp[so_metric] != False) & (df_temp[so_metric] != 'False') & (df_temp[so_metric] != 'FALSE')
    
    if df[gpt_metric].dtype == bool:
        pass  # Already boolean
    elif set(df[gpt_metric].dropna().unique()).issubset({True, False, 'True', 'False', 'TRUE', 'FALSE'}):
        df_temp[gpt_metric] = df_temp[gpt_metric].map(lambda x: x == True or x == 'True' or x == 'TRUE')
    else:
        # For non-boolean categorical, convert to boolean based on presence of any value
        df_temp[gpt_metric] = df_temp[gpt_metric].notna() & (df_temp[gpt_metric] != False) & (df_temp[gpt_metric] != 'False') & (df_temp[gpt_metric] != 'FALSE')
    
    contingency_table = pd.crosstab(df_temp[so_metric], df_temp[gpt_metric])
    
    # Ensure the contingency table has the proper structure with False/True values
    # Convert index and columns to string to avoid boolean indexing issues
    contingency_table.index = contingency_table.index.astype(str)
    contingency_table.columns = contingency_table.columns.astype(str)
    
    # Add missing rows and columns
    for idx in ['False', 'True']:
        if idx not in contingency_table.index:
            contingency_table.loc[idx] = 0
    for col in ['False', 'True']:
        if col not in contingency_table.columns:
            contingency_table[col] = 0
    
    # Sort to ensure consistent order
    contingency_table = contingency_table.reindex(index=['False', 'True'], columns=['False', 'True'])
    
    # Perform statistical tests - use Fisher's exact test instead of chi-square for small counts
    try:
        # Check if chi-square test is appropriate (expected frequencies > 5)
        row_totals = contingency_table.sum(axis=1)
        col_totals = contingency_table.sum(axis=0)
        n = contingency_table.sum().sum()
        
        # Calculate expected frequencies
        expected = np.outer(row_totals, col_totals) / n
        
        # Determine which test to use
        use_fisher = np.any(expected < 1)
        
        if use_fisher:
            # Use Fisher's exact test
            oddsratio, p = fisher_exact(contingency_table)
            test_name = "Fisher's exact test"
            test_stat = oddsratio
        else:
            # Use chi-square test
            chi2, p, dof, expected = chi2_contingency(contingency_table)
            test_name = "Chi-square test"
            test_stat = chi2
        
        # Calculate association metrics
        association_metrics = calculate_association_metrics(contingency_table)
        
        if association_metrics:
            # Extract metric name (remove "(so)" suffix)
            metric_name = so_metric.replace('(so)', '')
            
            # Store results
            result = {
                'Metric': metric_name,
                'Test': test_name,
                'Test Statistic': test_stat,
                'p-value': p,
                'Significant': 'Yes' if p < 0.05 else 'No',
                'Phi Coefficient': association_metrics['phi'],
                'Odds Ratio': association_metrics['odds_ratio'],
                'P(GPT|SO)': association_metrics['p_gpt_given_so'],
                'P(GPT|~SO)': association_metrics['p_gpt_given_not_so'],
                'P(SO|GPT)': association_metrics['p_so_given_gpt'],
                'P(SO|~GPT)': association_metrics['p_so_given_not_gpt'],
                'GPT Rate (%)': association_metrics['gpt_rate'],
                'SO Rate (%)': association_metrics['so_rate'],
                'Difference (SO-GPT)': association_metrics['difference']
            }
            all_results.append(result)
            
            # Store distribution data for visualization
            distribution_data.append({
                'Metric': metric_name,
                'Both': association_metrics['counts']['both'],
                'Only GPT': association_metrics['counts']['only_gpt'],
                'Only SO': association_metrics['counts']['only_so'],
                'Neither': association_metrics['counts']['neither'],
                'Both (%)': association_metrics['both_pct'],
                'Only GPT (%)': association_metrics['only_gpt_pct'],
                'Only SO (%)': association_metrics['only_so_pct'],
                'Neither (%)': association_metrics['neither_pct']
            })
            
            # Print results
            print(f"\nMetric: {metric_name}")
            print(f"  {test_name}: {test_stat:.4f}")
            print(f"  p-value: {p:.4f}")
            print(f"  Significant: {'Yes' if p < 0.05 else 'No'}")
            print(f"  Phi Coefficient: {association_metrics['phi']:.4f}")
            print(f"  Odds Ratio: {association_metrics['odds_ratio']:.4f}")
            print(f"  Contingency table:")
            print(contingency_table)
            print(f"  Conditional Probabilities:")
            print(f"    P(GPT has TD | SO has TD) = {association_metrics['p_gpt_given_so']:.4f}")
            print(f"    P(GPT has TD | SO has no TD) = {association_metrics['p_gpt_given_not_so']:.4f}")
            print(f"    P(SO has TD | GPT has TD) = {association_metrics['p_so_given_gpt']:.4f}")
            print(f"    P(SO has TD | GPT has no TD) = {association_metrics['p_so_given_not_gpt']:.4f}")
            print(f"  TD Distribution:")
            print(f"    Both have TD: {association_metrics['counts']['both']} cases ({association_metrics['both_pct']:.2f}%)")
            print(f"    Only GPT has TD: {association_metrics['counts']['only_gpt']} cases ({association_metrics['only_gpt_pct']:.2f}%)")
            print(f"    Only SO has TD: {association_metrics['counts']['only_so']} cases ({association_metrics['only_so_pct']:.2f}%)")
            print(f"    Neither has TD: {association_metrics['counts']['neither']} cases ({association_metrics['neither_pct']:.2f}%)")
            print(f"  TD Rates:")
            print(f"    GPT TD Rate: {association_metrics['gpt_rate']:.2f}%")
            print(f"    SO TD Rate: {association_metrics['so_rate']:.2f}%")
            print(f"    Difference (SO - GPT): {association_metrics['difference']:.2f} percentage points")
    
    except Exception as e:
        print(f"Error performing analysis for {so_metric} vs {gpt_metric}: {e}")
        print(f"Contingency table: \n{contingency_table}")

# Create summary dataframe and visualizations if results exist
if all_results:
    # Create summary dataframe
    results_df = pd.DataFrame(all_results)
    print("\n--- Analysis Results Summary ---")
    print(results_df.to_string())
    
    # Create distributions dataframe
    distributions_df = pd.DataFrame(distribution_data)
    
    # ------- Create Visualizations -------
    
    # 1. Test Statistic and Phi Coefficient visualization
    plt.figure(figsize=(14, 7))
    metrics = results_df['Metric'].tolist()
    x = np.arange(len(metrics))
    width = 0.3
    
    # Create two-part bar chart
    ax = plt.subplot(111)
    # Use a normalized test statistic for better visualization across different tests
    normalized_stats = []
    for i, test in enumerate(results_df['Test']):
        if test == "Chi-square test":
            normalized_stats.append(results_df['Test Statistic'].iloc[i] / 10)  # Scale down chi-square
        else:
            normalized_stats.append(min(results_df['Test Statistic'].iloc[i], 5))  # Cap odds ratio at 5
    
    ax.bar(x - width/2, normalized_stats, width, label='Test Statistic (normalized)', color='#1f77b4')
    ax.set_ylabel('Test Statistic Value')
    ax.set_title('Statistical Test Results and Phi Coefficients by Metric')
    ax.set_xticks(x)
    ax.set_xticklabels(metrics, rotation=45, ha='right')
    
    # Create secondary y-axis for Phi coefficient
    ax2 = ax.twinx()
    ax2.bar(x + width/2, results_df['Phi Coefficient'], width, label='Phi Coefficient', color='#ff7f0e')
    ax2.set_ylabel('Phi Coefficient')
    
    for i, is_sig in enumerate(results_df['Significant']):
        if is_sig == 'Yes':
            ax.text(i, normalized_stats[i] + 0.2, '*', 
                    fontsize=16, ha='center', va='bottom', color='red')
    
    # Add legend
    lines1, labels1 = ax.get_legend_handles_labels()
    lines2, labels2 = ax2.get_legend_handles_labels()
    ax.legend(lines1 + lines2, labels1 + labels2, loc='upper right')
    
    plt.tight_layout()
    plt.savefig('statistical_test_and_phi_results.png', dpi=300)
    print("\nStatistical test and Phi coefficient visualization saved as 'statistical_test_and_phi_results.png'")
    
    # 2. Technical Debt Distribution visualization
    plt.figure(figsize=(16, 8))
    
    # Create stacked bar chart for distribution
    metrics = distributions_df['Metric'].tolist()
    x = np.arange(len(metrics))
    width = 0.7
    
    # Stacked bars
    plt.bar(x, distributions_df['Both (%)'], width, label='Both have TD', color=custom_palette['Both'])
    plt.bar(x, distributions_df['Only GPT (%)'], width, bottom=distributions_df['Both (%)'], 
            label='Only GPT has TD', color=custom_palette['GPT'])
    plt.bar(x, distributions_df['Only SO (%)'], width, 
            bottom=distributions_df['Both (%)'] + distributions_df['Only GPT (%)'], 
            label='Only SO has TD', color=custom_palette['SO'])
    plt.bar(x, distributions_df['Neither (%)'], width, 
            bottom=distributions_df['Both (%)'] + distributions_df['Only GPT (%)'] + distributions_df['Only SO (%)'], 
            label='Neither has TD', color=custom_palette['Neither'])
    
    # Add labels and title
    plt.ylabel('Percentage of Cases')
    plt.title('Technical Debt Distribution by Metric')
    plt.xticks(x, metrics, rotation=45, ha='right')
    plt.legend(loc='upper left', bbox_to_anchor=(1, 1))
    
    # Add percentage labels on each segment (only for segments > 5%)
    for i, metric in enumerate(metrics):
        # Variables to track the bottom of each bar
        bottom = 0
        for category, color_key in [('Both (%)', 'Both'), ('Only GPT (%)', 'GPT'), 
                             ('Only SO (%)', 'SO'), ('Neither (%)', 'Neither')]:
            value = distributions_df.loc[distributions_df['Metric'] == metric, category].values[0]
            if value > 5:  # Only add text if segment is > 5%
                plt.text(i, bottom + value/2, f'{value:.1f}%', ha='center', va='center', 
                        fontweight='bold', color='black')
            bottom += value
    
    plt.tight_layout()
    plt.savefig('technical_debt_distribution.png', dpi=300)
    print("\nTechnical debt distribution visualization saved as 'technical_debt_distribution.png'")
    
    # 3. Prevalence Comparison visualization
    plt.figure(figsize=(14, 7))
    
    # Create grouped bar chart
    x = np.arange(len(metrics))
    width = 0.35
    
    plt.bar(x - width/2, results_df['GPT Rate (%)'], width, label='GPT TD Rate', color=custom_palette['GPT'])
    plt.bar(x + width/2, results_df['SO Rate (%)'], width, label='SO TD Rate', color=custom_palette['SO'])
    
    # Add text showing the difference
    for i, metric in enumerate(metrics):
        diff = results_df.loc[results_df['Metric'] == metric, 'Difference (SO-GPT)'].values[0]
        if abs(diff) > 2:  # Only show difference if it's more than 2 percentage points
            color = 'green' if diff < 0 else 'red'  # Green if GPT is better (less TD), red if SO is better
            plt.text(i, max(results_df.loc[i, 'GPT Rate (%)'], results_df.loc[i, 'SO Rate (%)']) + 2,
                    f"{diff:+.1f}pp", ha='center', va='bottom', color=color, fontweight='bold')
    
    # Add labels and title
    plt.ylabel('Technical Debt Rate (%)')
    plt.title('Technical Debt Prevalence: GPT vs Stack Overflow')
    plt.xticks(x, metrics, rotation=45, ha='right')
    plt.legend()
    
    plt.tight_layout()
    plt.savefig('td_prevalence_comparison.png', dpi=300)
    print("\nTechnical debt prevalence comparison saved as 'td_prevalence_comparison.png'")
    
    # 4. Odds ratio and conditional probabilities visualization
    plt.figure(figsize=(16, 12))
    
    # Create a 2x2 subplot grid
    fig, axes = plt.subplots(2, 2, figsize=(16, 12))
    
    # 4a. Odds ratio (upper left)
    ax1 = axes[0, 0]
    odds_ratios = results_df['Odds Ratio'].values
    # Cap extremely large values for visualization
    odds_ratios_capped = [min(x, 10) for x in odds_ratios]
    
    # Use plt.bar directly with metrics for x values
    bars = ax1.bar(range(len(metrics)), odds_ratios_capped, color='#ff7f0e')
    ax1.set_title('Odds Ratio by Metric (capped at 10)')
    ax1.set_ylabel('Odds Ratio')
    # Set x-ticks properly
    ax1.set_xticks(range(len(metrics)))
    ax1.set_xticklabels(metrics, rotation=45, ha='right')
    
    # Add text for actual values for capped bars
    for i, (val, capped) in enumerate(zip(odds_ratios, odds_ratios_capped)):
        if val > 10:
            ax1.text(i, capped, f"({val:.1f})", ha='center', va='bottom', color='red')
    
    # 4b. P(GPT|SO) vs P(GPT|~SO) (upper right)
    ax2 = axes[0, 1]
    width = 0.35
    x = np.arange(len(metrics))
    
    ax2.bar(x - width/2, results_df['P(GPT|SO)'], width, label='P(GPT has TD | SO has TD)')
    ax2.bar(x + width/2, results_df['P(GPT|~SO)'], width, label='P(GPT has TD | SO has no TD)')
    ax2.set_title('Conditional Probability: GPT given SO')
    ax2.set_ylabel('Probability')
    ax2.set_xticks(x)
    ax2.set_xticklabels(metrics, rotation=45, ha='right')
    ax2.legend()
    
    # 4c. P(SO|GPT) vs P(SO|~GPT) (lower left)
    ax3 = axes[1, 0]
    
    ax3.bar(x - width/2, results_df['P(SO|GPT)'], width, label='P(SO has TD | GPT has TD)')
    ax3.bar(x + width/2, results_df['P(SO|~GPT)'], width, label='P(SO has TD | GPT has no TD)')
    ax3.set_title('Conditional Probability: SO given GPT')
    ax3.set_ylabel('Probability')
    ax3.set_xticks(x)
    ax3.set_xticklabels(metrics, rotation=45, ha='right')
    ax3.legend()
    
    # 4d. Both conditional probability differences (lower right)
    ax4 = axes[1, 1]
    
    diff1 = results_df['P(GPT|SO)'] - results_df['P(GPT|~SO)']
    diff2 = results_df['P(SO|GPT)'] - results_df['P(SO|~GPT)']
    
    ax4.bar(x - width/2, diff1, width, label='P(GPT|SO) - P(GPT|~SO)')
    ax4.bar(x + width/2, diff2, width, label='P(SO|GPT) - P(SO|~GPT)')
    ax4.set_title('Conditional Probability Differences')
    ax4.set_ylabel('Probability Difference')
    ax4.set_xticks(x)
    ax4.set_xticklabels(metrics, rotation=45, ha='right')
    ax4.legend()
    ax4.axhline(y=0, color='grey', linestyle='--', alpha=0.7)
    
    plt.tight_layout()
    plt.savefig('conditional_probabilities.png', dpi=300)
    print("\nConditional probabilities visualization saved as 'conditional_probabilities.png'")
    
   # 5. In-Depth Analysis of Overall Technical Debt (TD)
if 'TD' in results_df['Metric'].values:
    # Extract TD-specific data
    td_data = results_df[results_df['Metric'] == 'TD'].iloc[0]
    td_dist = distributions_df[distributions_df['Metric'] == 'TD'].iloc[0]
    
    # Create figure with more space between subplots
    fig, ax = plt.subplots(2, 2, figsize=(16, 14))  # Increased figure size
    plt.subplots_adjust(wspace=0.3, hspace=0.4)  # Add more space between subplots
    
    # 5a. TD Distribution (upper left)
    labels = ['Both have TD', 'Only GPT has TD', 'Only SO has TD', 'Neither has TD']
    sizes = [td_dist['Both (%)'], td_dist['Only GPT (%)'], td_dist['Only SO (%)'], td_dist['Neither (%)']]
    colors = [custom_palette['Both'], custom_palette['GPT'], custom_palette['SO'], custom_palette['Neither']]
    
    ax[0, 0].pie(sizes, labels=None, colors=colors, autopct='%1.1f%%', startangle=90)
    ax[0, 0].set_title('Technical Debt Distribution', fontsize=14, pad=20)  # Add padding to title
    
    # Fix the count for "Only SO has TD" if needed
    so_only_count = int(td_dist['Only SO'])
    # Add legend with counts - position adjusted to avoid overlap
    legend_labels = [
        f"Both have TD ({int(td_dist['Both'])})",
        f"Only GPT has TD ({int(td_dist['Only GPT'])})",
        f"Only SO has TD ({so_only_count})",
        f"Neither has TD ({int(td_dist['Neither'])})"
    ]
    ax[0, 0].legend(legend_labels, loc='center left', bbox_to_anchor=(1, 0.5))
    
    # 5b. TD Prevalence Comparison (upper right)
    x_labels = ['GPT', 'Stack Overflow']
    heights = [td_data['GPT Rate (%)'], td_data['SO Rate (%)']]
    ax[0, 1].bar(x_labels, heights, color=[custom_palette['GPT'], custom_palette['SO']])
    ax[0, 1].set_title('Technical Debt Prevalence', fontsize=14, pad=20)  # Add padding to title
    ax[0, 1].set_ylabel('Percentage of Solutions with TD')
    
    # Add text showing the difference - position adjusted to avoid overlap with title
    diff = td_data['Difference (SO-GPT)']
    color = 'green' if diff > 0 else 'red'  # Green if SO has more TD (GPT is better), red otherwise
    
    # Position the difference text at the top of the plot with more space
    y_max = max(heights)
    ax[0, 1].set_ylim(0, y_max * 1.2)  # Extend y-axis to make room for text
    ax[0, 1].text(0.5, y_max * 1.15, f"Difference: {diff:.1f}pp", 
                 ha='center', va='center', color=color, fontweight='bold', fontsize=12)
    
    # 5c. Conditional Probabilities (lower left)
    x_labels = ['P(GPT|SO)', 'P(GPT|~SO)', 'P(SO|GPT)', 'P(SO|~GPT)']
    heights = [td_data['P(GPT|SO)'], td_data['P(GPT|~SO)'], td_data['P(SO|GPT)'], td_data['P(SO|~GPT)']]
    colors = [custom_palette['GPT'], custom_palette['GPT'], custom_palette['SO'], custom_palette['SO']]
    ax[1, 0].bar(x_labels, heights, color=colors)
    ax[1, 0].set_title('Conditional Probabilities', fontsize=14, pad=20)  # Add padding to title
    ax[1, 0].set_ylabel('Probability')
    
    # Add text for each bar - improved positioning
    for i, h in enumerate(heights):
        ax[1, 0].text(i, h/2, f"{h*100:.1f}%", ha='center', va='center', color='white', fontweight='bold')
    
    # 5d. Association Metrics (lower right) - FIXED HERE
    # Create separate y-axis scales for different metrics
    ax1 = ax[1, 1]
    ax1.set_title('Association Metrics', fontsize=14, pad=20)  # Add padding to title
    
    # Plot bars with appropriate spacing
    bar_width = 0.6
    x_positions = [0, 1, 2]
    
    # Plot Test Statistic on primary y-axis
    test_stat_bar = ax1.bar(x_positions[0], td_data['Test Statistic'], bar_width, color='#1f77b4')
    ax1.set_ylabel('Test Statistic Value', fontsize=12)
    
    # Create secondary y-axis for Phi Coefficient and Odds Ratio
    ax2 = ax1.twinx()
    phi_bar = ax2.bar(x_positions[1], td_data['Phi Coefficient'], bar_width, color='#ff7f0e')
    odds_bar = ax2.bar(x_positions[2], td_data['Odds Ratio'], bar_width, color='#2ca02c')
    ax2.set_ylabel('Coefficient/Ratio Value', fontsize=12)
    
    # Set x-axis labels
    ax1.set_xticks(x_positions)
    ax1.set_xticklabels(['Test Statistic', 'Phi Coefficient', 'Odds Ratio'])
    
    # Add text labels for each value
    ax1.text(x_positions[0], td_data['Test Statistic']/2, f"{td_data['Test Statistic']:.2f}", 
             ha='center', va='center', color='white', fontweight='bold')
    ax2.text(x_positions[1], td_data['Phi Coefficient']/2, f"{td_data['Phi Coefficient']:.2f}", 
             ha='center', va='center', color='black', fontweight='bold')
    ax2.text(x_positions[2], td_data['Odds Ratio']/2, f"{td_data['Odds Ratio']:.2f}", 
             ha='center', va='center', color='white', fontweight='bold')
    
    # Add legend - position it ABOVE the plot to avoid overlap
    legend_elements = [
        plt.Rectangle((0,0), 1, 1, color='#1f77b4', label='Test Statistic'),
        plt.Rectangle((0,0), 1, 1, color='#ff7f0e', label='Phi Coefficient'),
        plt.Rectangle((0,0), 1, 1, color='#2ca02c', label='Odds Ratio')
    ]
    ax1.legend(handles=legend_elements, loc='upper center', bbox_to_anchor=(0.5, 1.15), 
              ncol=3, frameon=False)
    
    # Ensure proper scaling for secondary y-axis
    ax2.set_ylim(0, max(td_data['Phi Coefficient'], td_data['Odds Ratio']) * 1.2)
    
    plt.tight_layout(pad=4.0)  # Increased padding between subplots
    plt.savefig('overall_td_analysis.png', dpi=300, bbox_inches='tight')
    print("\nOverall technical debt analysis visualization saved as 'overall_td_analysis.png'")
    
else:
    print("\nNo analysis results to visualize.")

# Print the final contingency table for TD
print("\nContingency table for final TD(so) vs TD(gpt)!")
contingency_table = pd.crosstab(df['TD(so)'], df['TD(gpt)'], margins=True, margins_name="Total")
print("\nContingency Table:")
print(contingency_table)