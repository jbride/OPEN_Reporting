select qualification.id as 'Qualification ID', qualification.name as 'Qualification Name', assessment.id as 'Assessment ID', assessment.name as 'Assessment Name' 
from qualification, assessment, qualification_rules
where qualification_rules.qualification_id=qualification.id
and qualification_rules.assessment_id=assessment.id
order by qualification.id;