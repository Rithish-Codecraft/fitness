import type { AnimationPattern } from './exerciseCatalog';

export interface ExercisePhaseCues {
  eccentric: string;
  concentric: string;
  peak: string;
}

export interface ExerciseMediaEntry {
  id: string;
  name: string;
  mediaUrl?: string;
  fallbackGifUrl?: string;
  primaryMuscles: string[];
  secondaryMuscles: string[];
  muscleGroupKey: string;
  equipment: string;
  animationType: AnimationPattern;
  defaultWeightKg: number;
  defaultReps: number;
  phaseCues: ExercisePhaseCues;
  formCues: string[];
}

export const EXERCISE_LIBRARY: ExerciseMediaEntry[] = [
  {
    id: 'incline-barbell-bench-press',
    name: 'Incline Barbell Bench Press',
    primaryMuscles: ['Upper Pectorals'],
    secondaryMuscles: ['Anterior Deltoids', 'Triceps Brachii'],
    muscleGroupKey: 'Chest / Pectorals',
    equipment: 'Barbell + Incline Bench',
    animationType: 'BENCH_PRESS',
    defaultWeightKg: 60,
    defaultReps: 8,
    phaseCues: {
      eccentric: 'Eccentric: Control descent 2s to upper chest (45° tuck)',
      concentric: 'Concentric: Drive bar up without flaring elbows',
      peak: 'Peak: Lock ribs down & squeeze upper pecs'
    },
    formCues: [
      'Retract shoulder blades firmly into the pad',
      'Lower bar to upper chest with 45° elbow tuck',
      'Press explosively while maintaining wrist alignment'
    ]
  },
  {
    id: 'standing-cable-chest-fly',
    name: 'Standing Cable Chest Fly',
    primaryMuscles: ['Pectoralis Major'],
    secondaryMuscles: ['Anterior Deltoids', 'Rectus Abdominis'],
    muscleGroupKey: 'Chest / Pectorals',
    equipment: 'Dual Cable Pulley',
    animationType: 'CABLE_FLY',
    defaultWeightKg: 15,
    defaultReps: 12,
    phaseCues: {
      eccentric: 'Eccentric: Open arms wide under tension with 15° elbow bend',
      concentric: 'Concentric: Sweep handles inward along lower-pec arc',
      peak: 'Peak: Squeeze inner chest for 1s at midline'
    },
    formCues: [
      'Maintain a soft 15° elbow bend throughout the arc',
      'Step into a staggered stance to stabilize torso',
      'Bring wrists together at sternum level without shrugging'
    ]
  },
  {
    id: 'seated-lat-pulldown',
    name: 'Seated Lat Pulldown',
    primaryMuscles: ['Latissimus Dorsi'],
    secondaryMuscles: ['Biceps Brachii', 'Rhomboids', 'Rear Deltoids'],
    muscleGroupKey: 'Lats & Upper Back',
    equipment: 'Cable Pulley Machine',
    animationType: 'LAT_PULLDOWN',
    defaultWeightKg: 55,
    defaultReps: 10,
    phaseCues: {
      eccentric: 'Eccentric: Extend arms overhead for a full lat stretch',
      concentric: 'Concentric: Depress scapulae & drive elbows toward hips',
      peak: 'Peak: Hold bar at upper clavicle & squeeze lats'
    },
    formCues: [
      'Lock thighs under pad and lean back ~15°',
      'Initiate the pull by depressing shoulder blades first',
      'Pull elbows down rather than curling with the wrists'
    ]
  },
  {
    id: 'bent-over-barbell-row',
    name: 'Bent-Over Barbell Row',
    primaryMuscles: ['Latissimus Dorsi', 'Rhomboids'],
    secondaryMuscles: ['Biceps Brachii', 'Erector Spinae', 'Hamstrings'],
    muscleGroupKey: 'Lats & Upper Back',
    equipment: 'Barbell',
    animationType: 'BARBELL_ROW',
    defaultWeightKg: 60,
    defaultReps: 10,
    phaseCues: {
      eccentric: 'Eccentric: Lower bar under control without rounding spine',
      concentric: 'Concentric: Row bar to navel by driving elbows back',
      peak: 'Peak: Pinch shoulder blades together for 1s'
    },
    formCues: [
      'Hinge hips back to a 45° torso angle with neutral spine',
      'Keep bar skimming close to thighs',
      'Avoid jerking torso upward during concentric drive'
    ]
  },
  {
    id: 'standing-overhead-barbell-press',
    name: 'Standing Overhead Barbell Press',
    primaryMuscles: ['Anterior & Lateral Deltoids'],
    secondaryMuscles: ['Triceps Brachii', 'Upper Trapezius', 'Core'],
    muscleGroupKey: 'Shoulders / Deltoids',
    equipment: 'Barbell',
    animationType: 'OVERHEAD_PRESS',
    defaultWeightKg: 40,
    defaultReps: 8,
    phaseCues: {
      eccentric: 'Eccentric: Lower bar smoothly back to upper clavicle',
      concentric: 'Concentric: Brace glutes & press vertically overhead',
      peak: 'Peak: Push head slightly through at full lockout'
    },
    formCues: [
      'Squeeze glutes and abs to prevent lumbar hyperextension',
      'Keep forearms vertical directly under the bar',
      'Press in a straight vertical path past the chin'
    ]
  },
  {
    id: 'dumbbell-lateral-raise',
    name: 'Dumbbell Lateral Raise',
    primaryMuscles: ['Lateral Deltoids'],
    secondaryMuscles: ['Upper Trapezius', 'Core'],
    muscleGroupKey: 'Shoulders / Deltoids',
    equipment: 'Dumbbells',
    animationType: 'LATERAL_RAISE',
    defaultWeightKg: 10,
    defaultReps: 15,
    phaseCues: {
      eccentric: 'Eccentric: Resist gravity for 2s down to sides',
      concentric: 'Concentric: Lead with elbows in 30° scapular plane',
      peak: 'Peak: Pause at shoulder height without shrugging'
    },
    formCues: [
      'Angle arms ~30° forward in the scapular plane',
      'Keep traps relaxed and lead with the elbows',
      'Stop at shoulder height for pure side-delt tension'
    ]
  },
  {
    id: 'standing-dumbbell-bicep-curl',
    name: 'Standing Dumbbell Bicep Curl',
    primaryMuscles: ['Biceps Brachii'],
    secondaryMuscles: ['Brachialis', 'Forearm Flexors'],
    muscleGroupKey: 'Biceps',
    equipment: 'Dumbbells',
    animationType: 'BICEP_CURL',
    defaultWeightKg: 14,
    defaultReps: 12,
    phaseCues: {
      eccentric: 'Eccentric: Lower 2–3s to full elbow extension',
      concentric: 'Concentric: Pin elbows to ribs & supinate upward',
      peak: 'Peak: Squeeze biceps hard at top without shoulder swing'
    },
    formCues: [
      'Anchor elbows tight to the sides of your ribcage',
      'Rotate palms upward (supinate) as you curl',
      'Avoid swinging the lower back for momentum'
    ]
  },
  {
    id: 'cable-rope-tricep-pushdown',
    name: 'Cable Rope Tricep Pushdown',
    primaryMuscles: ['Triceps Brachii'],
    secondaryMuscles: ['Anterior Deltoids', 'Core'],
    muscleGroupKey: 'Triceps',
    equipment: 'Cable Pulley Machine',
    animationType: 'TRICEP_PUSHDOWN',
    defaultWeightKg: 25,
    defaultReps: 12,
    phaseCues: {
      eccentric: 'Eccentric: Control rope up to 90° elbow flexion',
      concentric: 'Concentric: Extend elbows down & flare rope ends',
      peak: 'Peak: Lock triceps fully at bottom for 1s'
    },
    formCues: [
      'Keep upper arms locked vertically against your ribs',
      'Flare the rope apart at the bottom for lateral head activation',
      'Stop the return at 90° so shoulders stay neutral'
    ]
  },
  {
    id: 'barbell-back-squat',
    name: 'Barbell Back Squat',
    primaryMuscles: ['Quadriceps'],
    secondaryMuscles: ['Gluteus Maximus', 'Hamstrings', 'Core'],
    muscleGroupKey: 'Quadriceps',
    equipment: 'Barbell + Squat Rack',
    animationType: 'SQUAT',
    defaultWeightKg: 80,
    defaultReps: 8,
    phaseCues: {
      eccentric: 'Eccentric: Sit hips down & back to parallel under control',
      concentric: 'Concentric: Drive evenly through mid-foot & keep chest tall',
      peak: 'Peak: Stack hips under bar & brace for next rep'
    },
    formCues: [
      'Brace core with a 360° breath before descending',
      'Track knees cleanly over toes',
      'Maintain neutral spine from top to bottom'
    ]
  },
  {
    id: '45-plate-loaded-leg-press',
    name: '45° Plate-Loaded Leg Press',
    primaryMuscles: ['Quadriceps'],
    secondaryMuscles: ['Gluteus Maximus', 'Hamstrings'],
    muscleGroupKey: 'Quadriceps',
    equipment: '45° Leg Press Sled',
    animationType: 'LEG_PRESS',
    defaultWeightKg: 140,
    defaultReps: 10,
    phaseCues: {
      eccentric: 'Eccentric: Lower sled until knees reach 95° flexion',
      concentric: 'Concentric: Press through heels & mid-foot smoothly',
      peak: 'Peak: Stop just short of hard knee lockout'
    },
    formCues: [
      'Keep lower back and pelvis glued to the backrest',
      'Avoid lifting heels off the footplate',
      'Control the sled descent for 2 full seconds'
    ]
  },
  {
    id: 'romanian-deadlift',
    name: 'Romanian Deadlift (RDL)',
    primaryMuscles: ['Hamstrings'],
    secondaryMuscles: ['Gluteus Maximus', 'Erector Spinae'],
    muscleGroupKey: 'Hamstrings',
    equipment: 'Barbell',
    animationType: 'ROMANIAN_DEADLIFT',
    defaultWeightKg: 70,
    defaultReps: 10,
    phaseCues: {
      eccentric: 'Eccentric: Push hips straight back keeping bar on thighs',
      concentric: 'Concentric: Snap hips forward via hamstring tension',
      peak: 'Peak: Stand tall without hyperextending lower back'
    },
    formCues: [
      'Maintain a soft 15° knee bend throughout',
      'Stop descending once hips stop traveling backward',
      'Keep lats engaged so bar stays close to legs'
    ]
  },
  {
    id: 'barbell-hip-thrust',
    name: 'Barbell Hip Thrust',
    primaryMuscles: ['Gluteus Maximus'],
    secondaryMuscles: ['Hamstrings', 'Quadriceps', 'Core'],
    muscleGroupKey: 'Glutes',
    equipment: 'Barbell + Bench',
    animationType: 'HIP_THRUST',
    defaultWeightKg: 90,
    defaultReps: 10,
    phaseCues: {
      eccentric: 'Eccentric: Lower hips smoothly without losing rib tuck',
      concentric: 'Concentric: Drive through heels to horizontal torso',
      peak: 'Peak: Hold posterior pelvic tilt & glute squeeze 1s'
    },
    formCues: [
      'Position shoulder blades squarely on bench edge',
      'Keep shins vertical at top lockout',
      'Tuck chin slightly to avoid lower-back arching'
    ]
  },
  {
    id: 'standing-calf-raise',
    name: 'Standing Calf Raise',
    primaryMuscles: ['Gastrocnemius', 'Soleus'],
    secondaryMuscles: [],
    muscleGroupKey: 'Calves',
    equipment: 'Pin-Selected Machine',
    animationType: 'CALF_RAISE',
    defaultWeightKg: 60,
    defaultReps: 15,
    phaseCues: {
      eccentric: 'Eccentric: Drop heels deep for a 2s Achilles stretch',
      concentric: 'Concentric: Drive straight up onto big toes',
      peak: 'Peak: Hold top plantarflexion for 1 full second'
    },
    formCues: [
      'Avoid bouncing at the bottom stretch',
      'Keep knees softly straight for gastrocnemius focus',
      'Full range of motion on every rep'
    ]
  },
  {
    id: 'hanging-leg-raise',
    name: 'Hanging Leg Raise / Crunch',
    primaryMuscles: ['Rectus Abdominis'],
    secondaryMuscles: ['Obliques', 'Hip Flexors'],
    muscleGroupKey: 'Abs & Core',
    equipment: 'Bodyweight / Bar',
    animationType: 'CRUNCH_PLANK',
    defaultWeightKg: 0,
    defaultReps: 15,
    phaseCues: {
      eccentric: 'Eccentric: Uncurl pelvis slowly without swinging',
      concentric: 'Concentric: Curl pelvis upward toward lower ribs',
      peak: 'Peak: Exhale forcefully & hold abdominal crunch'
    },
    formCues: [
      'Initiate every rep by curling the pelvis, not swinging legs',
      'Exhale at peak contraction for deeper core engagement',
      'Control the descent to eliminate momentum'
    ]
  }
];

export function resolveExerciseMedia(exerciseName: string, muscleGroup: string): ExerciseMediaEntry {
  const lower = exerciseName.toLowerCase().trim();

  const exact = EXERCISE_LIBRARY.find((e) => e.name.toLowerCase() === lower);
  if (exact) return exact;

  const keywordRules: Array<[RegExp, string]> = [
    [/bench|chest press|push.?up|dip/, 'incline-barbell-bench-press'],
    [/fly|pec deck|crossover/, 'standing-cable-chest-fly'],
    [/pulldown|pull.?up|chin.?up/, 'seated-lat-pulldown'],
    [/row|t-bar/, 'bent-over-barbell-row'],
    [/overhead|military|arnold|shoulder press/, 'standing-overhead-barbell-press'],
    [/lateral|side raise/, 'dumbbell-lateral-raise'],
    [/curl|bicep|preacher|hammer/, 'standing-dumbbell-bicep-curl'],
    [/tricep|pushdown|skullcrusher|extension/, 'cable-rope-tricep-pushdown'],
    [/squat|lunge|split squat|goblet/, 'barbell-back-squat'],
    [/leg press|hack squat|leg extension/, '45-plate-loaded-leg-press'],
    [/rdl|romanian|deadlift|hamstring|leg curl/, 'romanian-deadlift'],
    [/hip thrust|glute|bridge|kickback/, 'barbell-hip-thrust'],
    [/calf|calves/, 'standing-calf-raise'],
    [/crunch|plank|ab|leg raise|sit.?up/, 'hanging-leg-raise']
  ];

  for (const [pattern, id] of keywordRules) {
    if (pattern.test(lower)) {
      const found = EXERCISE_LIBRARY.find((e) => e.id === id);
      if (found) return found;
    }
  }

  const byMuscle = EXERCISE_LIBRARY.find((e) => e.muscleGroupKey === muscleGroup);
  if (byMuscle) {
    return {
      ...byMuscle,
      name: exerciseName.trim() || byMuscle.name
    };
  }

  return EXERCISE_LIBRARY[0];
}
