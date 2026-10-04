import csv
from pathlib import Path
import matplotlib.pyplot as plt

with open("results/results.csv", newline="") as file:
    rows = list(csv.DictReader(file))

Path("results/plots").mkdir(parents=True, exist_ok=True)
plt.rcParams.update({"font.size": 12, "axes.titlesize": 14, "legend.fontsize": 9})
titles = {"W1": "Random access", "W2": "Search", "W3": "Insert and remove", "W4": "Priority processing"}
colors = ["#1764ab", "#d26520", "#278552", "#9853a1"]

for workload, title in titles.items():
    selected = [row for row in rows if row["workload"] == workload]
    groups = list(dict.fromkeys((row["structure"], row["variant"]) for row in selected))
    figure, axes = plt.subplots(2, 2, figsize=(10, 5.4))
    figure.subplots_adjust(left=0.085, right=0.98, top=0.87, bottom=0.21, hspace=0.7, wspace=0.3)
    figure.suptitle(workload + " - " + title, fontsize=16)
    for axis, metric in zip(axes.flat, ["time_ms", "steps", "moves", "comparisons"]):
        for i, (structure, variant) in enumerate(groups):
            values = [row for row in selected if row["structure"] == structure and row["variant"] == variant]
            label = structure if variant == "-" else structure + " " + variant
            axis.plot([int(row["n"]) for row in values], [float(row[metric]) for row in values],
                      marker=["o", "s", "^", "D"][i], color=colors[i], label=label,
                      linestyle=["-", "--", "-.", ":"][i], markersize=6, markerfacecolor="none")
        axis.set_xscale("log")
        axis.set_xticks([100, 1000, 10000, 100000], ["100", "1k", "10k", "100k"])
        axis.set_xlabel("n (elements)")
        axis.set_title("Median time" if metric == "time_ms" else metric.capitalize())
        axis.set_ylabel("Time (ms)" if metric == "time_ms" else "Count")
        if any(float(row[metric]) > 0 for row in selected):
            axis.set_yscale("log")
        else:
            axis.set_ylim(-0.1, 1)
            axis.set_yticks([0])
            axis.text(0.5, 0.5, "All counts = 0", transform=axis.transAxes, ha="center", fontsize=11)
        axis.grid(True, alpha=0.25)
    handles, labels = axes[0, 0].get_legend_handles_labels()
    figure.legend(handles, labels, loc="lower center", ncol=2, frameon=False, fontsize=10)
    figure.savefig("results/plots/" + workload.lower() + ".png", dpi=180)
    plt.close(figure)
