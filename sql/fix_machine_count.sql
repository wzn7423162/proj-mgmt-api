-- 修复项目表中的机台数量统计
-- 将 machine_count 更新为实际的机台数量

UPDATE t_project p
SET machine_count = (
    SELECT COUNT(*)
    FROM t_machine m
    WHERE m.project_id = p.id
      AND m.del_flag = '0'
),
update_time = NOW()
WHERE del_flag = '0';

-- 查看修复结果
SELECT
    id,
    project_name,
    machine_count,
    (SELECT COUNT(*) FROM t_machine WHERE project_id = t_project.id AND del_flag = '0') AS actual_count
FROM t_project
WHERE del_flag = '0';

